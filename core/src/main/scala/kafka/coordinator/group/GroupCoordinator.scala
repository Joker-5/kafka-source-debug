/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kafka.coordinator.group

import kafka.common.OffsetAndMetadata
import kafka.log.LogConfig
import kafka.message.ProducerCompressionCodec
import kafka.server._
import kafka.utils.Logging
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.internals.Topic
import org.apache.kafka.common.message.JoinGroupResponseData.JoinGroupResponseMember
import org.apache.kafka.common.message.LeaveGroupRequestData.MemberIdentity
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.metrics.stats.Meter
import org.apache.kafka.common.protocol.{ApiKeys, Errors}
import org.apache.kafka.common.requests._
import org.apache.kafka.common.utils.Time

import java.util.Properties
import java.util.concurrent.atomic.AtomicBoolean
import scala.collection.{Map, Seq, Set, immutable, mutable}
import scala.math.max

/**
 * GroupCoordinator handles general group membership and offset management.
 *
 * Each Kafka server instantiates a coordinator which is responsible for a set of
 * groups. Groups are assigned to coordinators based on their group names.
 * <p>
 * <b>Delayed operation locking notes:</b>
 * Delayed operations in GroupCoordinator use `group` as the delayed operation
 * lock. ReplicaManager.appendRecords may be invoked while holding the group lock
 * used by its callback.  The delayed callback may acquire the group lock
 * since the delayed operation is completed only if the group lock can be acquired.
 */
class GroupCoordinator(val brokerId: Int,
                       val groupConfig: GroupConfig,
                       val offsetConfig: OffsetConfig,
                       val groupManager: GroupMetadataManager,
                       val heartbeatPurgatory: DelayedOperationPurgatory[DelayedHeartbeat],
                       val rebalancePurgatory: DelayedOperationPurgatory[DelayedRebalance],
                       time: Time,
                       metrics: Metrics) extends Logging {
  import GroupCoordinator._

  type JoinCallback = JoinGroupResult => Unit
  type SyncCallback = SyncGroupResult => Unit

  /* setup metrics */
  val offsetDeletionSensor = metrics.sensor("OffsetDeletions")

  offsetDeletionSensor.add(new Meter(
    metrics.metricName("offset-deletion-rate",
      "group-coordinator-metrics",
      "The rate of administrative deleted offsets"),
    metrics.metricName("offset-deletion-count",
      "group-coordinator-metrics",
      "The total number of administrative deleted offsets")))

  val groupCompletedRebalanceSensor = metrics.sensor("CompletedRebalances")

  groupCompletedRebalanceSensor.add(new Meter(
    metrics.metricName("group-completed-rebalance-rate",
      "group-coordinator-metrics",
      "The rate of completed rebalance"),
    metrics.metricName("group-completed-rebalance-count",
      "group-coordinator-metrics",
      "The total number of completed rebalance")))

  this.logIdent = "[GroupCoordinator " + brokerId + "]: "

  private val isActive = new AtomicBoolean(false)

  def offsetsTopicConfigs: Properties = {
    val props = new Properties
    props.put(LogConfig.CleanupPolicyProp, LogConfig.Compact)
    props.put(LogConfig.SegmentBytesProp, offsetConfig.offsetsTopicSegmentBytes.toString)
    props.put(LogConfig.CompressionTypeProp, ProducerCompressionCodec.name)

    props
  }

  /**
   * NOTE: If a group lock and metadataLock are simultaneously needed,
   * be sure to acquire the group lock before metadataLock to prevent deadlock
   */

  /**
   * Startup logic executed at the same time when the server starts up.
   */
  def startup(retrieveGroupMetadataTopicPartitionCount: () => Int, enableMetadataExpiration: Boolean = true): Unit = {
    info("Starting up.")
    groupManager.startup(retrieveGroupMetadataTopicPartitionCount, enableMetadataExpiration)
    isActive.set(true)
    info("Startup complete.")
  }

  /**
   * Shutdown logic executed at the same time when server shuts down.
   * Ordering of actions should be reversed from the startup process.
   */
  def shutdown(): Unit = {
    info("Shutting down.")
    isActive.set(false)
    groupManager.shutdown()
    heartbeatPurgatory.shutdown()
    rebalancePurgatory.shutdown()
    info("Shutdown complete.")
  }

  /**
   * Verify if the group has space to accept the joining member. The various
   * criteria are explained below.
   */
  private def acceptJoiningMember(group: GroupMetadata, member: String): Boolean = {
    group.currentState match {
      // Always accept the request when the group is empty or dead
      case Empty | Dead =>
        true

      // An existing member is accepted if it is already awaiting. New members are accepted
      // up to the max group size. Note that the number of awaiting members is used here
      // for two reasons:
      // 1) the group size is not reliable as it could already be above the max group size
      //    if the max group size was reduced.
      // 2) using the number of awaiting members allows to kick out the last rejoining
      //    members of the group.
      case PreparingRebalance =>
        (group.has(member) && group.get(member).isAwaitingJoin) ||
          group.numAwaiting < groupConfig.groupMaxSize

      // An existing member is accepted. New members are accepted up to the max group size.
      // Note that the group size is used here. When the group transitions to CompletingRebalance,
      // members which haven't rejoined are removed.
      case CompletingRebalance | Stable =>
        group.has(member) || group.size < groupConfig.groupMaxSize
    }
  }

  /**
   * 处理加入消费者组请求的具体逻辑实现
   * @param groupId 消费者组名
   * @param memberId 消费者组成员 Id。如果成员是新加入的，那么该字段是空字符串
   * @param groupInstanceId 组静态成员 Id
   * @param requireKnownMemberId 是否要求成员 Id 不为空
   * @param clientId 消费者端参数 client.id 值
   * @param clientHost 消费者程序的主机名
   * @param rebalanceTimeoutMs Rebalance 超时时间
   * @param sessionTimeoutMs 会话超时时间
   * @param protocolType 协议类型
   * @param protocols 按照分配策略分组的订阅分区
   * @param responseCallback 完成加入组之后的回调逻辑方法
   * @param requestLocal
   */
  def handleJoinGroup(groupId: String,
                      memberId: String,
                      groupInstanceId: Option[String],
                      requireKnownMemberId: Boolean,
                      clientId: String,
                      clientHost: String,
                      rebalanceTimeoutMs: Int,
                      sessionTimeoutMs: Int,
                      protocolType: String,
                      protocols: List[(String, Array[Byte])],
                      responseCallback: JoinCallback,
                      requestLocal: RequestLocal = RequestLocal.NoCaching): Unit = {
    // 验证消费者组状态的合法性
    validateGroupStatus(groupId, ApiKeys.JOIN_GROUP).foreach { error =>
      responseCallback(JoinGroupResult(memberId, error))
      return
    }

    // 确保 sessionTimeoutMs 介于[group.min.session.timeout.ms值，group.max.session.timeout.ms值]之间
    if (sessionTimeoutMs < groupConfig.groupMinSessionTimeoutMs ||
      sessionTimeoutMs > groupConfig.groupMaxSessionTimeoutMs) {
      // 否则抛出异常，表示超时时间设置无效
      responseCallback(JoinGroupResult(memberId, Errors.INVALID_SESSION_TIMEOUT))
    } else {
      // 消费者组成员 Id 是否为空
      val isUnknownMember = memberId == JoinGroupRequest.UNKNOWN_MEMBER_ID
      // group is created if it does not exist and the member id is UNKNOWN. if member
      // is specified but group does not exist, request is rejected with UNKNOWN_MEMBER_ID
      // 获取消费者组信息，如果组不存在，就创建一个新的消费者组
      groupManager.getOrMaybeCreateGroup(groupId, isUnknownMember) match {
        // 消费者组不存在，抛出异常
        case None =>
          responseCallback(JoinGroupResult(memberId, Errors.UNKNOWN_MEMBER_ID))
        // 消费者组存在  
        case Some(group) =>
          group.inLock {
            // 如果该消费者组已满员
            if (!acceptJoiningMember(group, memberId)) {
              // 移除该消费者组成员
              group.remove(memberId)
              // 封装异常表明组已满员
              responseCallback(JoinGroupResult(JoinGroupRequest.UNKNOWN_MEMBER_ID, Errors.GROUP_MAX_SIZE_REACHED))
            // 如果消费者组成员 Id 为空  
            } else if (isUnknownMember) {
              // 为空 Id 成员执行加入组操作
              doNewMemberJoinGroup(
                group,
                groupInstanceId,
                requireKnownMemberId,
                clientId,
                clientHost,
                rebalanceTimeoutMs,
                sessionTimeoutMs,
                protocolType,
                protocols,
                responseCallback,
                requestLocal
              )
            } else {
              // 为非空 Id 成员执行加入组操作
              doCurrentMemberJoinGroup(
                group,
                memberId,
                groupInstanceId,
                clientId,
                clientHost,
                rebalanceTimeoutMs,
                sessionTimeoutMs,
                protocolType,
                protocols,
                responseCallback
              )
            }

            // attempt to complete JoinGroup
            // 如果消费者组正处于 PreparingRebalance 状态
            if (group.is(PreparingRebalance)) {
              // 将请求放入 Purgatory，等待后面统一延时处理
              rebalancePurgatory.checkAndComplete(GroupJoinKey(group.groupId))
            }
          }
      }
    }
  }

  /**
   * 负责处理全新的消费者组成员(它们的 Member ID 尚未生成)加入组的逻辑
   * @param group 消费者组元数据
   * @param groupInstanceId 组静态成员Id
   * @param requireKnownMemberId 是否要求成员 Id 不为空
   * @param clientId 消费者端参数 client.id 值
   * @param clientHost 消费者程序的主机名
   * @param rebalanceTimeoutMs Rebalance 超时时间
   * @param sessionTimeoutMs 会话超时时间
   * @param protocolType 协议类型
   * @param protocols 按照分配策略分组的订阅分区
   * @param responseCallback 完成加入组之后的回调逻辑方法
   * @param requestLocal
   */
  private def doNewMemberJoinGroup(
    group: GroupMetadata,
    groupInstanceId: Option[String],
    requireKnownMemberId: Boolean,
    clientId: String,
    clientHost: String,
    rebalanceTimeoutMs: Int,
    sessionTimeoutMs: Int,
    protocolType: String,
    protocols: List[(String, Array[Byte])],
    responseCallback: JoinCallback,
    requestLocal: RequestLocal
  ): Unit = {
    group.inLock {
      // 消费者组处于 Dead 状态
      if (group.is(Dead)) {
        // if the group is marked as dead, it means some other thread has just removed the group
        // from the coordinator metadata; it is likely that the group has migrated to some other
        // coordinator OR the group is in a transient unstable phase. Let the member retry
        // finding the correct coordinator and rejoin.
        // 封装异常调用回调函数返回
        responseCallback(JoinGroupResult(JoinGroupRequest.UNKNOWN_MEMBER_ID, Errors.COORDINATOR_NOT_AVAILABLE))
      // 成员配置的协议类型/分区消费分配策略与消费者组的不匹配  
      } else if (!group.supportsProtocols(protocolType, MemberMetadata.plainProtocolSet(protocols))) {
        responseCallback(JoinGroupResult(JoinGroupRequest.UNKNOWN_MEMBER_ID, Errors.INCONSISTENT_GROUP_PROTOCOL))
      } else {
        // 根据规则为该成员创建成员 Id
        val newMemberId = group.generateMemberId(clientId, groupInstanceId)
        groupInstanceId match {
          // 配置了静态成员
          case Some(instanceId) =>
            doStaticNewMemberJoinGroup(
              group,
              instanceId,
              newMemberId,
              clientId,
              clientHost,
              rebalanceTimeoutMs,
              sessionTimeoutMs,
              protocolType,
              protocols,
              responseCallback,
              requestLocal
            )
          // 没有配置静态成员  
          case None =>
            // 执行非静态成员加入消费者组逻辑
            doDynamicNewMemberJoinGroup(
              group,
              requireKnownMemberId,
              newMemberId,
              clientId,
              clientHost,
              rebalanceTimeoutMs,
              sessionTimeoutMs,
              protocolType,
              protocols,
              responseCallback
            )
        }
      }
    }
  }

  private def doStaticNewMemberJoinGroup(
    group: GroupMetadata,
    groupInstanceId: String,
    newMemberId: String,
    clientId: String,
    clientHost: String,
    rebalanceTimeoutMs: Int,
    sessionTimeoutMs: Int,
    protocolType: String,
    protocols: List[(String, Array[Byte])],
    responseCallback: JoinCallback,
    requestLocal: RequestLocal
  ): Unit = {
    group.currentStaticMemberId(groupInstanceId) match {
      case Some(oldMemberId) =>
        info(s"Static member with groupInstanceId=$groupInstanceId and unknown member id joins " +
          s"group ${group.groupId} in ${group.currentState} state. Replacing previously mapped " +
          s"member $oldMemberId with this groupInstanceId.")
        updateStaticMemberAndRebalance(group, oldMemberId, newMemberId, groupInstanceId, protocols, responseCallback, requestLocal)

      case None =>
        info(s"Static member with groupInstanceId=$groupInstanceId and unknown member id joins " +
          s"group ${group.groupId} in ${group.currentState} state. Created a new member id $newMemberId " +
          s"for this member and add to the group.")
        addMemberAndRebalance(rebalanceTimeoutMs, sessionTimeoutMs, newMemberId, Some(groupInstanceId),
          clientId, clientHost, protocolType, protocols, group, responseCallback)
    }
  }

  /**
   * 处理非静态成员加入消费者组逻辑
   * @param group
   * @param requireKnownMemberId
   * @param newMemberId
   * @param clientId
   * @param clientHost
   * @param rebalanceTimeoutMs
   * @param sessionTimeoutMs
   * @param protocolType
   * @param protocols
   * @param responseCallback
   */
  private def doDynamicNewMemberJoinGroup(
    group: GroupMetadata,
    requireKnownMemberId: Boolean,
    newMemberId: String,
    clientId: String,
    clientHost: String,
    rebalanceTimeoutMs: Int,
    sessionTimeoutMs: Int,
    protocolType: String,
    protocols: List[(String, Array[Byte])],
    responseCallback: JoinCallback
  ): Unit = {
    // 如果要求成员ID不为空
    if (requireKnownMemberId) {
      // If member id required, register the member in the pending member list and send
      // back a response to call for another join group request with allocated member id.
      info(s"Dynamic member with unknown member id joins group ${group.groupId} in " +
        s"${group.currentState} state. Created a new member id $newMemberId and request the " +
        s"member to rejoin with this id.")
      group.addPendingMember(newMemberId)
      addPendingMemberExpiration(group, newMemberId, sessionTimeoutMs)
      responseCallback(JoinGroupResult(newMemberId, Errors.MEMBER_ID_REQUIRED))
    } else {
      info(s"Dynamic Member with unknown member id joins group ${group.groupId} in " +
        s"${group.currentState} state. Created a new member id $newMemberId for this member " +
        s"and add to the group.")
      // 添加成员
      addMemberAndRebalance(rebalanceTimeoutMs, sessionTimeoutMs, newMemberId, None,
        clientId, clientHost, protocolType, protocols, group, responseCallback)
    }
  }

  private def validateCurrentMember(
    group: GroupMetadata,
    memberId: String,
    groupInstanceId: Option[String],
    operation: String
  ): Option[Errors] = {
    // We are validating two things:
    // 1. If `groupInstanceId` is present, then it exists and is mapped to `memberId`
    // 2. The `memberId` exists in the group
    groupInstanceId.flatMap { instanceId =>
      group.currentStaticMemberId(instanceId) match {
        case Some(currentMemberId) if currentMemberId != memberId =>
          info(s"Request memberId=$memberId for static member with groupInstanceId=$instanceId " +
            s"is fenced by current memberId=$currentMemberId during operation $operation")
          Some(Errors.FENCED_INSTANCE_ID)
        case Some(_) =>
          None
        case None =>
          Some(Errors.UNKNOWN_MEMBER_ID)
      }
    }.orElse {
      if (!group.has(memberId)) {
        Some(Errors.UNKNOWN_MEMBER_ID)
      } else {
        None
      }
    }
  }

  /**
   * 为已经设置成员 ID 的成员执行加入组的逻辑
   * @param group 消费者组元数据
   * @param memberId
   * @param groupInstanceId
   * @param clientId
   * @param clientHost
   * @param rebalanceTimeoutMs
   * @param sessionTimeoutMs
   * @param protocolType
   * @param protocols
   * @param responseCallback
   */
  private def doCurrentMemberJoinGroup(
    group: GroupMetadata,
    memberId: String,
    groupInstanceId: Option[String],
    clientId: String,
    clientHost: String,
    rebalanceTimeoutMs: Int,
    sessionTimeoutMs: Int,
    protocolType: String,
    protocols: List[(String, Array[Byte])],
    responseCallback: JoinCallback
  ): Unit = {
    group.inLock {
      // 如果是 Dead 状态，封装 COORDINATOR_NOT_AVAILABLE 异常调用回调函数返回
      if (group.is(Dead)) {
        // if the group is marked as dead, it means some other thread has just removed the group
        // from the coordinator metadata; it is likely that the group has migrated to some other
        // coordinator OR the group is in a transient unstable phase. Let the member retry
        // finding the correct coordinator and rejoin.
        responseCallback(JoinGroupResult(memberId, Errors.COORDINATOR_NOT_AVAILABLE))
      // 如果协议类型或分区消费分配策略与消费者组的不匹配，则封装 INCONSISTENT_GROUP_PROTOCOL 异常调用回调函数返回
      } else if (!group.supportsProtocols(protocolType, MemberMetadata.plainProtocolSet(protocols))) {
        responseCallback(JoinGroupResult(memberId, Errors.INCONSISTENT_GROUP_PROTOCOL))
      // 如果是待决成员，由于这次分配了成员 Id，故允许加入组  
      } else if (group.isPendingMember(memberId)) {
        // A rejoining pending member will be accepted. Note that pending member cannot be a static member.
        groupInstanceId.foreach { instanceId =>
          throw new IllegalStateException(s"Received unexpected JoinGroup with groupInstanceId=$instanceId " +
            s"for pending member with memberId=$memberId")
        }

        debug(s"Pending dynamic member with id $memberId joins group ${group.groupId} in " +
          s"${group.currentState} state. Adding to the group now.")
        // 令其加入组
        addMemberAndRebalance(rebalanceTimeoutMs, sessionTimeoutMs, memberId, None,
          clientId, clientHost, protocolType, protocols, group, responseCallback)
      } else {
        val memberErrorOpt = validateCurrentMember(
          group,
          memberId,
          groupInstanceId,
          operation = "join-group"
        )

        memberErrorOpt match {
          case Some(error) => responseCallback(JoinGroupResult(memberId, error))

          case None => group.currentState match {
            // 如果是 PreparingRebalance 状态
            case PreparingRebalance =>
              // 获取该成员的元数据信息
              val member = group.get(memberId)
              // 更新成员信息并开始准备 Rebalance
              updateMemberAndRebalance(group, member, protocols, s"Member ${member.memberId} joining group during ${group.currentState}", responseCallback)

            // 如果是 CompletingRebalance 状态
            case CompletingRebalance =>
              val member = group.get(memberId)
              // 如果成员以前申请过加入组
              if (member.matches(protocols)) {
                // member is joining with the same metadata (which could be because it failed to
                // receive the initial JoinGroup response), so just return current group information
                // for the current generation.
                // 直接返回当前组信息
                responseCallback(JoinGroupResult(
                  members = if (group.isLeader(memberId)) {
                    group.currentMemberMetadata
                  } else {
                    List.empty
                  },
                  memberId = memberId,
                  generationId = group.generationId,
                  protocolType = group.protocolType,
                  protocolName = group.protocolName,
                  leaderId = group.leaderOrNull,
                  error = Errors.NONE))
              // 否则，更新成员信息并开始准备 Rebalance
              } else {
                // member has changed metadata, so force a rebalance
                updateMemberAndRebalance(group, member, protocols, s"Updating metadata for member ${member.memberId} during ${group.currentState}", responseCallback)
              }

            // 如果是 Stable 状态  
            case Stable =>
              val member = group.get(memberId)
              // 如果成员是 Leader 成员
              if (group.isLeader(memberId)) {
                // force a rebalance if the leader sends JoinGroup;
                // This allows the leader to trigger rebalances for changes affecting assignment
                // which do not affect the member metadata (such as topic metadata changes for the consumer)
                // 更新成员信息并开始准备 Rebalance
                updateMemberAndRebalance(group, member, protocols, s"Leader ${member.memberId} re-joining group during ${group.currentState}", responseCallback)
              // 如果成员变更了分区分配策略  
              } else if (!member.matches(protocols)) {
                // 更新成员信息并开始准备 Rebalance
                updateMemberAndRebalance(group, member, protocols, s"Updating metadata for member ${member.memberId} during ${group.currentState}", responseCallback)
              } else {
                // for followers with no actual change to their metadata, just return group information
                // for the current generation which will allow them to issue SyncGroup
                responseCallback(JoinGroupResult(
                  members = List.empty,
                  memberId = memberId,
                  generationId = group.generationId,
                  protocolType = group.protocolType,
                  protocolName = group.protocolName,
                  leaderId = group.leaderOrNull,
                  error = Errors.NONE))
              }

            // 如果是其它状态，封装异常调用回调函数返回  
            case Empty | Dead =>
              // Group reaches unexpected state. Let the joining member reset their generation and rejoin.
              warn(s"Attempt to add rejoining member $memberId of group ${group.groupId} in " +
                s"unexpected group state ${group.currentState}")
              responseCallback(JoinGroupResult(memberId, Errors.UNKNOWN_MEMBER_ID))
          }
        }
      }
    }
  }

  /**
   * 处理消费者组同步相关逻辑
   * @param groupId 消费者组名
   * @param generation 消费者组 Generation 号
   * @param memberId 消费者组成员 Id
   * @param protocolType 协议类型
   * @param protocolName 分区消费分配策略名称
   * @param groupInstanceId 组静态成员 Id
   * @param groupAssignment 按照成员分组的分配方案
   * @param responseCallback
   * @param requestLocal
   */
  def handleSyncGroup(groupId: String,
                      generation: Int,
                      memberId: String,
                      protocolType: Option[String],
                      protocolName: Option[String],
                      groupInstanceId: Option[String],
                      groupAssignment: Map[String, Array[Byte]],
                      responseCallback: SyncCallback,
                      requestLocal: RequestLocal = RequestLocal.NoCaching): Unit = {
    // 验证消费者组状态及合法性 
    validateGroupStatus(groupId, ApiKeys.SYNC_GROUP) match {
      // 如果未通过合法性检查，且错误原因是 Coordinator 正在加载，则封装 REBALANCE_IN_PROGRESS 异常，并调用回调函数返回
      case Some(error) if error == Errors.COORDINATOR_LOAD_IN_PROGRESS =>
        // The coordinator is loading, which means we've lost the state of the active rebalance and the
        // group will need to start over at JoinGroup. By returning rebalance in progress, the consumer
        // will attempt to rejoin without needing to rediscover the coordinator. Note that we cannot
        // return COORDINATOR_LOAD_IN_PROGRESS since older clients do not expect the error.
        responseCallback(SyncGroupResult(Errors.REBALANCE_IN_PROGRESS))

      // 如果是其它错误，则封装对应错误，并调用回调函数返回
      case Some(error) => responseCallback(SyncGroupResult(error))

      case None =>
        // 获取消费者组元数据
        groupManager.getGroup(groupId) match {
          // 如果未找到，则封装 UNKNOWN_MEMBER_ID 异常，并调用回调函数返回
          case None => responseCallback(SyncGroupResult(Errors.UNKNOWN_MEMBER_ID))
          // 如果找到的话，则调用 doSyncGroup 方法执行组同步任务
          case Some(group) => doSyncGroup(group, generation, memberId, protocolType, protocolName,
            groupInstanceId, groupAssignment, requestLocal, responseCallback)
        }
    }
  }

  private def validateSyncGroup(
    group: GroupMetadata,
    generationId: Int,
    memberId: String,
    protocolType: Option[String],
    protocolName: Option[String],
    groupInstanceId: Option[String],
  ): Option[Errors] = {
    if (group.is(Dead)) {
      // if the group is marked as dead, it means some other thread has just removed the group
      // from the coordinator metadata; this is likely that the group has migrated to some other
      // coordinator OR the group is in a transient unstable phase. Let the member retry
      // finding the correct coordinator and rejoin.
      Some(Errors.COORDINATOR_NOT_AVAILABLE)
    } else {
      validateCurrentMember(
        group,
        memberId,
        groupInstanceId,
        operation = "sync-group"
      ).orElse {
        if (generationId != group.generationId) {
          Some(Errors.ILLEGAL_GENERATION)
        } else if (protocolType.isDefined && !group.protocolType.contains(protocolType.get)) {
          Some(Errors.INCONSISTENT_GROUP_PROTOCOL)
        } else if (protocolName.isDefined && !group.protocolName.contains(protocolName.get)) {
          Some(Errors.INCONSISTENT_GROUP_PROTOCOL)
        } else {
          None
        }
      }
    }
  }

  /**
   * 执行真正的消费者组同步逻辑
   * @param group
   * @param generationId
   * @param memberId
   * @param protocolType
   * @param protocolName
   * @param groupInstanceId
   * @param groupAssignment
   * @param requestLocal
   * @param responseCallback
   */
  private def doSyncGroup(group: GroupMetadata,
                          generationId: Int,
                          memberId: String,
                          protocolType: Option[String],
                          protocolName: Option[String],
                          groupInstanceId: Option[String],
                          groupAssignment: Map[String, Array[Byte]],
                          requestLocal: RequestLocal,
                          responseCallback: SyncCallback): Unit = {
    group.inLock {
      // 消费者组同步前置合法性校验
      val validationErrorOpt = validateSyncGroup(
        group,
        generationId,
        memberId,
        protocolType,
        protocolName,
        groupInstanceId
      )

      validationErrorOpt match {
        // 如果存在错误，则调用回调函数返回响应错误
        case Some(error) => responseCallback(SyncGroupResult(error))

        case None => group.currentState match {
          // 封装 UNKNOWN_MEMBER_ID 异常，调用回调函数返回
          case Empty =>
            responseCallback(SyncGroupResult(Errors.UNKNOWN_MEMBER_ID))

          // 封装 REBALANCE_IN_PROGRESS 异常，调用回调函数返回  
          case PreparingRebalance =>
            responseCallback(SyncGroupResult(Errors.REBALANCE_IN_PROGRESS))

          case CompletingRebalance =>
            // 为该消费者组成员设置组同步回调函数
            group.get(memberId).awaitingSyncCallback = responseCallback
            removePendingSyncMember(group, memberId)

            // if this is the leader, then we can attempt to persist state and transition to stable
            // 组 Leader 成员发送的 SyncGroupRequest 请求需要特殊处理
            if (group.isLeader(memberId)) {
              info(s"Assignment received from leader $memberId for group ${group.groupId} for generation ${group.generationId}. " +
                s"The group has ${group.size} members, ${group.allStaticMembers.size} of which are static.")

              // fill any missing members with an empty assignment
              // 如果有成员没有被分配任何消费方案，则创建一个空的方案赋给它
              val missing = group.allMembers.diff(groupAssignment.keySet)
              val assignment = groupAssignment ++ missing.map(_ -> Array.empty[Byte]).toMap

              if (missing.nonEmpty) {
                warn(s"Setting empty assignments for members $missing of ${group.groupId} for generation ${group.generationId}")
              }

              // 把消费者组信息保存在消费者组元数据中，并且将其写入到内部位移主题
              groupManager.storeGroup(group, assignment, (error: Errors) => {
                group.inLock {
                  // another member may have joined the group while we were awaiting this callback,
                  // so we must ensure we are still in the CompletingRebalance state and the same generation
                  // when it gets invoked. if we have transitioned to another state, then do nothing
                  // 如果组状态是 CompletingRebalance 以及成员和组的 generationId 相同
                  if (group.is(CompletingRebalance) && generationId == group.generationId) {
                    // 如果有错误
                    if (error != Errors.NONE) {
                      // 清空分配方案并发送给所有成员
                      resetAndPropagateAssignmentError(group, error)
                      // 准备开启新一轮的Rebalance
                      maybePrepareRebalance(group, s"Error when storing group assignment during SyncGroup (member: $memberId)")
                    // 如果没有错误  
                    } else {
                      // 在消费者组元数据中保存分配方案并发送给所有成员
                      setAndPropagateAssignment(group, assignment)
                      // 变更消费者组状态到 Stable
                      group.transitionTo(Stable)
                    }
                  }
                }
              }, requestLocal)
              groupCompletedRebalanceSensor.record()
            }

          case Stable =>
            removePendingSyncMember(group, memberId)

            // if the group is stable, we just return the current assignment
            // 获取消费者组成员元数据
            val memberMetadata = group.get(memberId)
            // 封装组协议类型、分配策略、成员分配方案，调用回调函数返回
            responseCallback(SyncGroupResult(group.protocolType, group.protocolName, memberMetadata.assignment, Errors.NONE))
            // 设定成员下次心跳时间
            completeAndScheduleNextHeartbeatExpiration(group, group.get(memberId))

          // 直接抛异常  
          case Dead =>
            throw new IllegalStateException(s"Reached unexpected condition for Dead group ${group.groupId}")
        }
      }
    }
  }

  def handleLeaveGroup(groupId: String,
                       leavingMembers: List[MemberIdentity],
                       responseCallback: LeaveGroupResult => Unit): Unit = {

    def removeCurrentMemberFromGroup(group: GroupMetadata, memberId: String): Unit = {
      val member = group.get(memberId)
      removeMemberAndUpdateGroup(group, member, s"Removing member $memberId on LeaveGroup")
      removeHeartbeatForLeavingMember(group, member.memberId)
      info(s"Member $member has left group $groupId through explicit `LeaveGroup` request")
    }

    validateGroupStatus(groupId, ApiKeys.LEAVE_GROUP) match {
      case Some(error) =>
        responseCallback(leaveError(error, List.empty))
      case None =>
        groupManager.getGroup(groupId) match {
          case None =>
            responseCallback(leaveError(Errors.NONE, leavingMembers.map {leavingMember =>
              memberLeaveError(leavingMember, Errors.UNKNOWN_MEMBER_ID)
            }))
          case Some(group) =>
            group.inLock {
              if (group.is(Dead)) {
                responseCallback(leaveError(Errors.COORDINATOR_NOT_AVAILABLE, List.empty))
              } else {
                val memberErrors = leavingMembers.map { leavingMember =>
                  val memberId = leavingMember.memberId
                  val groupInstanceId = Option(leavingMember.groupInstanceId)

                  // The LeaveGroup API allows administrative removal of members by GroupInstanceId
                  // in which case we expect the MemberId to be undefined.
                  if (memberId == JoinGroupRequest.UNKNOWN_MEMBER_ID) {
                    groupInstanceId.flatMap(group.currentStaticMemberId) match {
                      case Some(currentMemberId) =>
                        removeCurrentMemberFromGroup(group, currentMemberId)
                        memberLeaveError(leavingMember, Errors.NONE)
                      case None =>
                        memberLeaveError(leavingMember, Errors.UNKNOWN_MEMBER_ID)
                    }
                  } else if (group.isPendingMember(memberId)) {
                    removePendingMemberAndUpdateGroup(group, memberId)
                    heartbeatPurgatory.checkAndComplete(MemberKey(group.groupId, memberId))
                    info(s"Pending member with memberId=$memberId has left group ${group.groupId} " +
                      s"through explicit `LeaveGroup` request")
                    memberLeaveError(leavingMember, Errors.NONE)
                  } else {
                    val memberError = validateCurrentMember(
                      group,
                      memberId,
                      groupInstanceId,
                      operation = "leave-group"
                    ).getOrElse {
                      removeCurrentMemberFromGroup(group, memberId)
                      Errors.NONE
                    }
                    memberLeaveError(leavingMember, memberError)
                  }
                }
                responseCallback(leaveError(Errors.NONE, memberErrors))
              }
            }
        }
    }
  }

  def handleDeleteGroups(groupIds: Set[String],
                         requestLocal: RequestLocal = RequestLocal.NoCaching): Map[String, Errors] = {
    val groupErrors = mutable.Map.empty[String, Errors]
    val groupsEligibleForDeletion = mutable.ArrayBuffer[GroupMetadata]()

    groupIds.foreach { groupId =>
      validateGroupStatus(groupId, ApiKeys.DELETE_GROUPS) match {
        case Some(error) =>
          groupErrors += groupId -> error

        case None =>
          groupManager.getGroup(groupId) match {
            case None =>
              groupErrors += groupId ->
                (if (groupManager.groupNotExists(groupId)) Errors.GROUP_ID_NOT_FOUND else Errors.NOT_COORDINATOR)
            case Some(group) =>
              group.inLock {
                group.currentState match {
                  case Dead =>
                    groupErrors += groupId ->
                      (if (groupManager.groupNotExists(groupId)) Errors.GROUP_ID_NOT_FOUND else Errors.NOT_COORDINATOR)
                  case Empty =>
                    group.transitionTo(Dead)
                    groupsEligibleForDeletion += group
                  case Stable | PreparingRebalance | CompletingRebalance =>
                    groupErrors(groupId) = Errors.NON_EMPTY_GROUP
                }
              }
          }
      }
    }

    if (groupsEligibleForDeletion.nonEmpty) {
      val offsetsRemoved = groupManager.cleanupGroupMetadata(groupsEligibleForDeletion, requestLocal,
        _.removeAllOffsets())
      groupErrors ++= groupsEligibleForDeletion.map(_.groupId -> Errors.NONE).toMap
      info(s"The following groups were deleted: ${groupsEligibleForDeletion.map(_.groupId).mkString(", ")}. " +
        s"A total of $offsetsRemoved offsets were removed.")
    }

    groupErrors
  }

  def handleDeleteOffsets(groupId: String, partitions: Seq[TopicPartition],
                          requestLocal: RequestLocal): (Errors, Map[TopicPartition, Errors]) = {
    var groupError: Errors = Errors.NONE
    var partitionErrors: Map[TopicPartition, Errors] = Map()
    var partitionsEligibleForDeletion: Seq[TopicPartition] = Seq()

    validateGroupStatus(groupId, ApiKeys.OFFSET_DELETE) match {
      case Some(error) =>
        groupError = error

      case None =>
        groupManager.getGroup(groupId) match {
          case None =>
            groupError = if (groupManager.groupNotExists(groupId))
              Errors.GROUP_ID_NOT_FOUND else Errors.NOT_COORDINATOR

          case Some(group) =>
            group.inLock {
              group.currentState match {
                case Dead =>
                  groupError = if (groupManager.groupNotExists(groupId))
                    Errors.GROUP_ID_NOT_FOUND else Errors.NOT_COORDINATOR

                case Empty =>
                  partitionsEligibleForDeletion = partitions

                case PreparingRebalance | CompletingRebalance | Stable if group.isConsumerGroup =>
                  val (consumed, notConsumed) =
                    partitions.partition(tp => group.isSubscribedToTopic(tp.topic()))

                  partitionsEligibleForDeletion = notConsumed
                  partitionErrors = consumed.map(_ -> Errors.GROUP_SUBSCRIBED_TO_TOPIC).toMap

                case _ =>
                  groupError = Errors.NON_EMPTY_GROUP
              }
            }

            if (partitionsEligibleForDeletion.nonEmpty) {
              val offsetsRemoved = groupManager.cleanupGroupMetadata(Seq(group), requestLocal,
                _.removeOffsets(partitionsEligibleForDeletion))

              partitionErrors ++= partitionsEligibleForDeletion.map(_ -> Errors.NONE).toMap

              offsetDeletionSensor.record(offsetsRemoved)

              info(s"The following offsets of the group $groupId were deleted: ${partitionsEligibleForDeletion.mkString(", ")}. " +
                s"A total of $offsetsRemoved offsets were removed.")
            }
        }
    }

    // If there is a group error, the partition errors is empty
    groupError -> partitionErrors
  }

  private def validateHeartbeat(
    group: GroupMetadata,
    generationId: Int,
    memberId: String,
    groupInstanceId: Option[String]
  ): Option[Errors] = {
    if (group.is(Dead)) {
      Some(Errors.COORDINATOR_NOT_AVAILABLE)
    } else {
      validateCurrentMember(
        group,
        memberId,
        groupInstanceId,
        operation = "heartbeat"
      ).orElse {
        if (generationId != group.generationId) {
          Some(Errors.ILLEGAL_GENERATION)
        } else {
          None
        }
      }
    }
  }

  // 心跳处理机制具体实现
  def handleHeartbeat(groupId: String,
                      memberId: String,
                      groupInstanceId: Option[String],
                      generationId: Int,
                      responseCallback: Errors => Unit): Unit = {
    validateGroupStatus(groupId, ApiKeys.HEARTBEAT).foreach { error =>
      if (error == Errors.COORDINATOR_LOAD_IN_PROGRESS)
        // the group is still loading, so respond just blindly
        responseCallback(Errors.NONE)
      else
        responseCallback(error)
      return
    }

    groupManager.getGroup(groupId) match {
      case None =>
        responseCallback(Errors.UNKNOWN_MEMBER_ID)

      case Some(group) => group.inLock {
        val validationErrorOpt = validateHeartbeat(
          group,
          generationId,
          memberId,
          groupInstanceId
        )

        if (validationErrorOpt.isDefined) {
          responseCallback(validationErrorOpt.get)
        } else {
          group.currentState match {
            case Empty =>
              responseCallback(Errors.UNKNOWN_MEMBER_ID)

            case CompletingRebalance =>
              // consumers may start sending heartbeat after join-group response, in which case
              // we should treat them as normal hb request and reset the timer
              val member = group.get(memberId)
              completeAndScheduleNextHeartbeatExpiration(group, member)
              responseCallback(Errors.NONE)

            case PreparingRebalance =>
                val member = group.get(memberId)
                completeAndScheduleNextHeartbeatExpiration(group, member)
                responseCallback(Errors.REBALANCE_IN_PROGRESS)

            case Stable =>
                val member = group.get(memberId)
                completeAndScheduleNextHeartbeatExpiration(group, member)
                responseCallback(Errors.NONE)

            case Dead =>
              throw new IllegalStateException(s"Reached unexpected condition for Dead group $groupId")
          }
        }
      }
    }
  }

  def handleTxnCommitOffsets(groupId: String,
                             producerId: Long,
                             producerEpoch: Short,
                             memberId: String,
                             groupInstanceId: Option[String],
                             generationId: Int,
                             offsetMetadata: immutable.Map[TopicPartition, OffsetAndMetadata],
                             responseCallback: immutable.Map[TopicPartition, Errors] => Unit,
                             requestLocal: RequestLocal = RequestLocal.NoCaching): Unit = {
    validateGroupStatus(groupId, ApiKeys.TXN_OFFSET_COMMIT) match {
      case Some(error) => responseCallback(offsetMetadata.map { case (k, _) => k -> error })
      case None =>
        val group = groupManager.getGroup(groupId).getOrElse {
          groupManager.addGroup(new GroupMetadata(groupId, Empty, time))
        }
        doTxnCommitOffsets(group, memberId, groupInstanceId, generationId, producerId, producerEpoch,
          offsetMetadata, requestLocal, responseCallback)
    }
  }

  def handleCommitOffsets(groupId: String,
                          memberId: String,
                          groupInstanceId: Option[String],
                          generationId: Int,
                          offsetMetadata: immutable.Map[TopicPartition, OffsetAndMetadata],
                          responseCallback: immutable.Map[TopicPartition, Errors] => Unit,
                          requestLocal: RequestLocal = RequestLocal.NoCaching): Unit = {
    validateGroupStatus(groupId, ApiKeys.OFFSET_COMMIT) match {
      case Some(error) => responseCallback(offsetMetadata.map { case (k, _) => k -> error })
      case None =>
        groupManager.getGroup(groupId) match {
          case None =>
            if (generationId < 0) {
              // the group is not relying on Kafka for group management, so allow the commit
              val group = groupManager.addGroup(new GroupMetadata(groupId, Empty, time))
              doCommitOffsets(group, memberId, groupInstanceId, generationId, offsetMetadata,
                responseCallback, requestLocal)
            } else {
              // or this is a request coming from an older generation. either way, reject the commit
              responseCallback(offsetMetadata.map { case (k, _) => k -> Errors.ILLEGAL_GENERATION })
            }

          case Some(group) =>
            doCommitOffsets(group, memberId, groupInstanceId, generationId, offsetMetadata,
              responseCallback, requestLocal)
        }
    }
  }

  def scheduleHandleTxnCompletion(producerId: Long,
                                  offsetsPartitions: Iterable[TopicPartition],
                                  transactionResult: TransactionResult): Unit = {
    require(offsetsPartitions.forall(_.topic == Topic.GROUP_METADATA_TOPIC_NAME))
    val isCommit = transactionResult == TransactionResult.COMMIT
    groupManager.scheduleHandleTxnCompletion(producerId, offsetsPartitions.map(_.partition).toSet, isCommit)
  }

  private def doTxnCommitOffsets(group: GroupMetadata,
                                 memberId: String,
                                 groupInstanceId: Option[String],
                                 generationId: Int,
                                 producerId: Long,
                                 producerEpoch: Short,
                                 offsetMetadata: immutable.Map[TopicPartition, OffsetAndMetadata],
                                 requestLocal: RequestLocal,
                                 responseCallback: immutable.Map[TopicPartition, Errors] => Unit): Unit = {
    group.inLock {
      val validationErrorOpt = validateOffsetCommit(
        group,
        generationId,
        memberId,
        groupInstanceId,
        isTransactional = true
      )

      if (validationErrorOpt.isDefined) {
        responseCallback(offsetMetadata.map { case (k, _) => k -> validationErrorOpt.get })
      } else {
        groupManager.storeOffsets(group, memberId, offsetMetadata, responseCallback, producerId,
          producerEpoch, requestLocal)
      }
    }
  }

  private def validateOffsetCommit(
    group: GroupMetadata,
    generationId: Int,
    memberId: String,
    groupInstanceId: Option[String],
    isTransactional: Boolean
  ): Option[Errors] = {
    if (group.is(Dead)) {
      Some(Errors.COORDINATOR_NOT_AVAILABLE)
    } else if (generationId >= 0 || memberId != JoinGroupRequest.UNKNOWN_MEMBER_ID || groupInstanceId.isDefined) {
      validateCurrentMember(
        group,
        memberId,
        groupInstanceId,
        operation = if (isTransactional) "txn-offset-commit" else "offset-commit"
      ).orElse {
        if (generationId != group.generationId) {
          Some(Errors.ILLEGAL_GENERATION)
        } else {
          None
        }
      }
    } else if (!isTransactional && !group.is(Empty)) {
      // When the group is non-empty, only members can commit offsets.
      // This does not apply to transactional offset commits, since the
      // older versions of this protocol do not require memberId and
      // generationId.
      Some(Errors.UNKNOWN_MEMBER_ID)
    } else {
      None
    }
  }

  private def doCommitOffsets(group: GroupMetadata,
                              memberId: String,
                              groupInstanceId: Option[String],
                              generationId: Int,
                              offsetMetadata: immutable.Map[TopicPartition, OffsetAndMetadata],
                              responseCallback: immutable.Map[TopicPartition, Errors] => Unit,
                              requestLocal: RequestLocal): Unit = {
    group.inLock {
      val validationErrorOpt = validateOffsetCommit(
        group,
        generationId,
        memberId,
        groupInstanceId,
        isTransactional = false
      )

      if (validationErrorOpt.isDefined) {
        responseCallback(offsetMetadata.map { case (k, _) => k -> validationErrorOpt.get })
      } else {
        group.currentState match {
          case Empty =>
            groupManager.storeOffsets(group, memberId, offsetMetadata, responseCallback)

          case Stable | PreparingRebalance =>
            // During PreparingRebalance phase, we still allow a commit request since we rely
            // on heartbeat response to eventually notify the rebalance in progress signal to the consumer
            val member = group.get(memberId)
            completeAndScheduleNextHeartbeatExpiration(group, member)
            groupManager.storeOffsets(group, memberId, offsetMetadata, responseCallback, requestLocal = requestLocal)

          case CompletingRebalance =>
            // We should not receive a commit request if the group has not completed rebalance;
            // but since the consumer's member.id and generation is valid, it means it has received
            // the latest group generation information from the JoinResponse.
            // So let's return a REBALANCE_IN_PROGRESS to let consumer handle it gracefully.
            responseCallback(offsetMetadata.map { case (k, _) => k -> Errors.REBALANCE_IN_PROGRESS })

          case _ =>
            throw new RuntimeException(s"Logic error: unexpected group state ${group.currentState}")
        }
      }
    }
  }

  def handleFetchOffsets(groupId: String, requireStable: Boolean, partitions: Option[Seq[TopicPartition]] = None):
  (Errors, Map[TopicPartition, OffsetFetchResponse.PartitionData]) = {

    validateGroupStatus(groupId, ApiKeys.OFFSET_FETCH) match {
      case Some(error) => error -> Map.empty
      case None =>
        // return offsets blindly regardless the current group state since the group may be using
        // Kafka commit storage without automatic group management
        (Errors.NONE, groupManager.getOffsets(groupId, requireStable, partitions))
    }
  }

  def handleListGroups(states: Set[String]): (Errors, List[GroupOverview]) = {
    if (!isActive.get) {
      (Errors.COORDINATOR_NOT_AVAILABLE, List[GroupOverview]())
    } else {
      val errorCode = if (groupManager.isLoading) Errors.COORDINATOR_LOAD_IN_PROGRESS else Errors.NONE
      // if states is empty, return all groups
      val groups = if (states.isEmpty)
        groupManager.currentGroups
      else
        groupManager.currentGroups.filter(g => states.contains(g.summary.state))
      (errorCode, groups.map(_.overview).toList)
    }
  }

  def handleDescribeGroup(groupId: String): (Errors, GroupSummary) = {
    validateGroupStatus(groupId, ApiKeys.DESCRIBE_GROUPS) match {
      case Some(error) => (error, GroupCoordinator.EmptyGroup)
      case None =>
        groupManager.getGroup(groupId) match {
          case None => (Errors.NONE, GroupCoordinator.DeadGroup)
          case Some(group) =>
            group.inLock {
              (Errors.NONE, group.summary)
            }
        }
    }
  }

  def handleDeletedPartitions(topicPartitions: Seq[TopicPartition], requestLocal: RequestLocal): Unit = {
    val offsetsRemoved = groupManager.cleanupGroupMetadata(groupManager.currentGroups, requestLocal,
      _.removeOffsets(topicPartitions))
    info(s"Removed $offsetsRemoved offsets associated with deleted partitions: ${topicPartitions.mkString(", ")}.")
  }

  /**
   * 校验消费者组名是否合法
   * @param groupId
   * @param api
   * @return
   */
  private def isValidGroupId(groupId: String, api: ApiKeys): Boolean = {
    api match {
      // Kafka 请求类型是 OFFSET_COMMIT、OFFSET_FETCH、DESCRIBE_GROUPS、DELETE_GROUPS，要求不为 null
      case ApiKeys.OFFSET_COMMIT | ApiKeys.OFFSET_FETCH | ApiKeys.DESCRIBE_GROUPS | ApiKeys.DELETE_GROUPS =>
        // For backwards compatibility, we support the offset commit APIs for the empty groupId, and also
        // in DescribeGroups and DeleteGroups so that users can view and delete state of all groups.
        groupId != null
      // 其他请求类型，要求不为 null 且不为空字符串  
      case _ =>
        // The remaining APIs are groups using Kafka for group coordination and must have a non-empty groupId
        groupId != null && !groupId.isEmpty
    }
  }

  /**
   * Check that the groupId is valid, assigned to this coordinator and that the group has been loaded.
   *
   * 校验消费者组状态的合法性
   * 
   * @param groupId
   * @param api
   * @return
   */
  private def validateGroupStatus(groupId: String, api: ApiKeys): Option[Errors] = {
    // 校验消费者组名是否合法
    if (!isValidGroupId(groupId, api))
      Some(Errors.INVALID_GROUP_ID)
    else if (!isActive.get)
      Some(Errors.COORDINATOR_NOT_AVAILABLE)
    else if (isCoordinatorLoadInProgress(groupId))
      Some(Errors.COORDINATOR_LOAD_IN_PROGRESS)
    else if (!isCoordinatorForGroup(groupId))
      Some(Errors.NOT_COORDINATOR)
    else
      None
  }

  private def onGroupUnloaded(group: GroupMetadata): Unit = {
    group.inLock {
      info(s"Unloading group metadata for ${group.groupId} with generation ${group.generationId}")
      val previousState = group.currentState
      group.transitionTo(Dead)

      previousState match {
        case Empty | Dead =>
        case PreparingRebalance =>
          for (member <- group.allMemberMetadata) {
            group.maybeInvokeJoinCallback(member, JoinGroupResult(member.memberId, Errors.NOT_COORDINATOR))
          }

          rebalancePurgatory.checkAndComplete(GroupJoinKey(group.groupId))

        case Stable | CompletingRebalance =>
          for (member <- group.allMemberMetadata) {
            group.maybeInvokeSyncCallback(member, SyncGroupResult(Errors.NOT_COORDINATOR))
            heartbeatPurgatory.checkAndComplete(MemberKey(group.groupId, member.memberId))
          }
      }

      removeSyncExpiration(group)
    }
  }

  private def onGroupLoaded(group: GroupMetadata): Unit = {
    group.inLock {
      info(s"Loading group metadata for ${group.groupId} with generation ${group.generationId}")
      assert(group.is(Stable) || group.is(Empty))
      if (groupIsOverCapacity(group)) {
        prepareRebalance(group, s"Freshly-loaded group is over capacity ($groupConfig.groupMaxSize). Rebalacing in order to give a chance for consumers to commit offsets")
      }

      group.allMemberMetadata.foreach(completeAndScheduleNextHeartbeatExpiration(group, _))
    }
  }

  /**
   * Load cached state from the given partition and begin handling requests for groups which map to it.
   *
   * @param offsetTopicPartitionId The partition we are now leading
   */
  def onElection(offsetTopicPartitionId: Int, coordinatorEpoch: Int): Unit = {
    info(s"Elected as the group coordinator for partition $offsetTopicPartitionId in epoch $coordinatorEpoch")
    groupManager.scheduleLoadGroupAndOffsets(offsetTopicPartitionId, coordinatorEpoch, onGroupLoaded)
  }

  /**
   * Unload cached state for the given partition and stop handling requests for groups which map to it.
   *
   * @param offsetTopicPartitionId The partition we are no longer leading
   */
  def onResignation(offsetTopicPartitionId: Int, coordinatorEpoch: Option[Int]): Unit = {
    info(s"Resigned as the group coordinator for partition $offsetTopicPartitionId in epoch $coordinatorEpoch")
    groupManager.removeGroupsForPartition(offsetTopicPartitionId, coordinatorEpoch, onGroupUnloaded)
  }

  private def setAndPropagateAssignment(group: GroupMetadata, assignment: Map[String, Array[Byte]]): Unit = {
    assert(group.is(CompletingRebalance))
    group.allMemberMetadata.foreach(member => member.assignment = assignment(member.memberId))
    propagateAssignment(group, Errors.NONE)
  }

  private def resetAndPropagateAssignmentError(group: GroupMetadata, error: Errors): Unit = {
    assert(group.is(CompletingRebalance))
    group.allMemberMetadata.foreach(_.assignment = Array.empty)
    propagateAssignment(group, error)
  }

  private def propagateAssignment(group: GroupMetadata, error: Errors): Unit = {
    val (protocolType, protocolName) = if (error == Errors.NONE)
      (group.protocolType, group.protocolName)
    else
      (None, None)
    for (member <- group.allMemberMetadata) {
      if (member.assignment.isEmpty && error == Errors.NONE) {
        warn(s"Sending empty assignment to member ${member.memberId} of ${group.groupId} for generation ${group.generationId} with no errors")
      }

      if (group.maybeInvokeSyncCallback(member, SyncGroupResult(protocolType, protocolName, member.assignment, error))) {
        // reset the session timeout for members after propagating the member's assignment.
        // This is because if any member's session expired while we were still awaiting either
        // the leader sync group or the storage callback, its expiration will be ignored and no
        // future heartbeat expectations will not be scheduled.
        completeAndScheduleNextHeartbeatExpiration(group, member)
      }
    }
  }

  /**
   * Complete existing DelayedHeartbeats for the given member and schedule the next one
   */
  private def completeAndScheduleNextHeartbeatExpiration(group: GroupMetadata, member: MemberMetadata): Unit = {
    completeAndScheduleNextExpiration(group, member, member.sessionTimeoutMs)
  }

  /**
   * Kafka心跳处理核心方法
   * 核心是创建一个心跳延迟任务DelayedHeartbeat，并对其检测是否完成或添加Watch
   * 启动心跳延迟或等待下一个心跳包到来
   *
   * 实现思路总结：
   * 1.开启一个延迟任务，延迟检查的时间为心跳过期时间，一旦延迟任务执行，意味着心跳超时
   * 2.当收到一个心跳包，需取消上一次设置的延迟任务
   * 3.使用可循环使用的延迟任务队列，从而实现类似定时任务的效果
   *
   * @param group 消费组元数据
   * @param member 消费者元数据
   * @param timeoutMs 心跳超时时间，由消费端「session.timeout.ms」设置，默认10s
   */
  private def completeAndScheduleNextExpiration(group: GroupMetadata, member: MemberMetadata, timeoutMs: Long): Unit = {
    // 1.为消费组设置唯一标识 groupId-memberId
    val memberKey = MemberKey(group.groupId, member.memberId)

    // complete current heartbeat expectation
    // 2.heartbeatSatisfied置为true，表示消费者收到一个有效的心跳包
    member.heartbeatSatisfied = true
    // 3.收到有效的心跳包，通知定时调度器停止本次的心跳过期检测
    heartbeatPurgatory.checkAndComplete(memberKey)

    // reschedule the next heartbeat expiration deadline
    // 4.构建一个DelayedHeartbeat，进入下一个心跳检测周期
    member.heartbeatSatisfied = false
    val delayedHeartbeat = new DelayedHeartbeat(this, group, member.memberId, isPending = false, timeoutMs)
    // 重要方法
    heartbeatPurgatory.tryCompleteElseWatch(delayedHeartbeat, Seq(memberKey))
  }

  /**
    * Add pending member expiration to heartbeat purgatory
    */
  private def addPendingMemberExpiration(group: GroupMetadata, pendingMemberId: String, timeoutMs: Long): Unit = {
    val pendingMemberKey = MemberKey(group.groupId, pendingMemberId)
    val delayedHeartbeat = new DelayedHeartbeat(this, group, pendingMemberId, isPending = true, timeoutMs)
    heartbeatPurgatory.tryCompleteElseWatch(delayedHeartbeat, Seq(pendingMemberKey))
  }

  private def removeHeartbeatForLeavingMember(group: GroupMetadata, memberId: String): Unit = {
    val memberKey = MemberKey(group.groupId, memberId)
    heartbeatPurgatory.checkAndComplete(memberKey)
  }

  /**
   * 向消费者组添加成员以及准备 Rebalance
   * @param rebalanceTimeoutMs
   * @param sessionTimeoutMs
   * @param memberId
   * @param groupInstanceId
   * @param clientId
   * @param clientHost
   * @param protocolType
   * @param protocols
   * @param group
   * @param callback
   */
  private def addMemberAndRebalance(rebalanceTimeoutMs: Int,
                                    sessionTimeoutMs: Int,
                                    memberId: String,
                                    groupInstanceId: Option[String],
                                    clientId: String,
                                    clientHost: String,
                                    protocolType: String,
                                    protocols: List[(String, Array[Byte])],
                                    group: GroupMetadata,
                                    callback: JoinCallback): Unit = {
    // 创建 MemberMetadata 对象实例
    val member = new MemberMetadata(memberId, groupInstanceId, clientId, clientHost,
      rebalanceTimeoutMs, sessionTimeoutMs, protocolType, protocols)

    // 标识该成员是新成员
    member.isNew = true

    // update the newMemberAdded flag to indicate that the join group can be further delayed
    // 如果消费者组准备开启首次 Rebalance，设置 newMemberAdded = True
    if (group.is(PreparingRebalance) && group.generationId == 0)
      group.newMemberAdded = true

    // 将该成员添加到消费者组
    group.add(member, callback)

    // The session timeout does not affect new members since they do not have their memberId and
    // cannot send heartbeats. Furthermore, we cannot detect disconnects because sockets are muted
    // while the JoinGroup is in purgatory. If the client does disconnect (e.g. because of a request
    // timeout during a long rebalance), they may simply retry which will lead to a lot of defunct
    // members in the rebalance. To prevent this going on indefinitely, we timeout JoinGroup requests
    // for new members. If the new member is still there, we expect it to retry.
    // 设置下次心跳超时时间
    completeAndScheduleNextExpiration(group, member, NewMemberJoinTimeoutMs)

    // 准备开启 Rebalance
    maybePrepareRebalance(group, s"Adding new member $memberId with group instance id $groupInstanceId")
  }

  private def updateStaticMemberAndRebalance(group: GroupMetadata,
                                             oldMemberId: String,
                                             newMemberId: String,
                                             groupInstanceId: String,
                                             protocols: List[(String, Array[Byte])],
                                             responseCallback: JoinCallback,
                                             requestLocal: RequestLocal): Unit = {
    val currentLeader = group.leaderOrNull
    val member = group.replaceStaticMember(groupInstanceId, oldMemberId, newMemberId)
    // Heartbeat of old member id will expire without effect since the group no longer contains that member id.
    // New heartbeat shall be scheduled with new member id.
    completeAndScheduleNextHeartbeatExpiration(group, member)

    val knownStaticMember = group.get(newMemberId)
    group.updateMember(knownStaticMember, protocols, responseCallback)
    val oldProtocols = knownStaticMember.supportedProtocols

    group.currentState match {
      case Stable =>
        // check if group's selectedProtocol of next generation will change, if not, simply store group to persist the
        // updated static member, if yes, rebalance should be triggered to let the group's assignment and selectProtocol consistent
        val selectedProtocolOfNextGeneration = group.selectProtocol
        if (group.protocolName.contains(selectedProtocolOfNextGeneration)) {
          info(s"Static member which joins during Stable stage and doesn't affect selectProtocol will not trigger rebalance.")
          val groupAssignment: Map[String, Array[Byte]] = group.allMemberMetadata.map(member => member.memberId -> member.assignment).toMap
          groupManager.storeGroup(group, groupAssignment, error => {
            if (error != Errors.NONE) {
              warn(s"Failed to persist metadata for group ${group.groupId}: ${error.message}")

              // Failed to persist member.id of the given static member, revert the update of the static member in the group.
              group.updateMember(knownStaticMember, oldProtocols, null)
              val oldMember = group.replaceStaticMember(groupInstanceId, newMemberId, oldMemberId)
              completeAndScheduleNextHeartbeatExpiration(group, oldMember)
              responseCallback(JoinGroupResult(
                List.empty,
                memberId = JoinGroupRequest.UNKNOWN_MEMBER_ID,
                generationId = group.generationId,
                protocolType = group.protocolType,
                protocolName = group.protocolName,
                leaderId = currentLeader,
                error = error
              ))
            } else {
              group.maybeInvokeJoinCallback(member, JoinGroupResult(
                members = List.empty,
                memberId = newMemberId,
                generationId = group.generationId,
                protocolType = group.protocolType,
                protocolName = group.protocolName,
                // We want to avoid current leader performing trivial assignment while the group
                // is in stable stage, because the new assignment in leader's next sync call
                // won't be broadcast by a stable group. This could be guaranteed by
                // always returning the old leader id so that the current leader won't assume itself
                // as a leader based on the returned message, since the new member.id won't match
                // returned leader id, therefore no assignment will be performed.
                leaderId = currentLeader,
                error = Errors.NONE))
            }
          }, requestLocal)
        } else {
          maybePrepareRebalance(group, s"Group's selectedProtocol will change because static member ${member.memberId} with instance id $groupInstanceId joined with change of protocol")
        }
      case CompletingRebalance =>
        // if the group is in after-sync stage, upon getting a new join-group of a known static member
        // we should still trigger a new rebalance, since the old member may already be sent to the leader
        // for assignment, and hence when the assignment gets back there would be a mismatch of the old member id
        // with the new replaced member id. As a result the new member id would not get any assignment.
        prepareRebalance(group, s"Updating metadata for static member ${member.memberId} with instance id $groupInstanceId")
      case Empty | Dead =>
        throw new IllegalStateException(s"Group ${group.groupId} was not supposed to be " +
          s"in the state ${group.currentState} when the unknown static member $groupInstanceId rejoins.")
      case PreparingRebalance =>
    }
  }

  private def updateMemberAndRebalance(group: GroupMetadata,
                                       member: MemberMetadata,
                                       protocols: List[(String, Array[Byte])],
                                       reason: String,
                                       callback: JoinCallback): Unit = {
    group.updateMember(member, protocols, callback)
    maybePrepareRebalance(group, reason)
  }

  private def maybePrepareRebalance(group: GroupMetadata, reason: String): Unit = {
    group.inLock {
      if (group.canRebalance)
        prepareRebalance(group, reason)
    }
  }

  // package private for testing
  private[group] def prepareRebalance(group: GroupMetadata, reason: String): Unit = {
    // if any members are awaiting sync, cancel their request and have them rejoin
    if (group.is(CompletingRebalance))
      resetAndPropagateAssignmentError(group, Errors.REBALANCE_IN_PROGRESS)

    // if a sync expiration is pending, cancel it.
    removeSyncExpiration(group)

    val delayedRebalance = if (group.is(Empty))
      new InitialDelayedJoin(this,
        rebalancePurgatory,
        group,
        groupConfig.groupInitialRebalanceDelayMs,
        groupConfig.groupInitialRebalanceDelayMs,
        max(group.rebalanceTimeoutMs - groupConfig.groupInitialRebalanceDelayMs, 0))
    else
      new DelayedJoin(this, group, group.rebalanceTimeoutMs)

    group.transitionTo(PreparingRebalance)

    info(s"Preparing to rebalance group ${group.groupId} in state ${group.currentState} with old generation " +
      s"${group.generationId} (${Topic.GROUP_METADATA_TOPIC_NAME}-${partitionFor(group.groupId)}) (reason: $reason)")

    val groupKey = GroupJoinKey(group.groupId)
    rebalancePurgatory.tryCompleteElseWatch(delayedRebalance, Seq(groupKey))
  }

  private def removeMemberAndUpdateGroup(group: GroupMetadata, member: MemberMetadata, reason: String): Unit = {
    // New members may timeout with a pending JoinGroup while the group is still rebalancing, so we have
    // to invoke the callback before removing the member. We return UNKNOWN_MEMBER_ID so that the consumer
    // will retry the JoinGroup request if is still active.
    group.maybeInvokeJoinCallback(member, JoinGroupResult(JoinGroupRequest.UNKNOWN_MEMBER_ID, Errors.UNKNOWN_MEMBER_ID))
    group.remove(member.memberId)

    group.currentState match {
      case Dead | Empty =>
      case Stable | CompletingRebalance => maybePrepareRebalance(group, reason)
      case PreparingRebalance => rebalancePurgatory.checkAndComplete(GroupJoinKey(group.groupId))
    }
  }

  private def removePendingMemberAndUpdateGroup(group: GroupMetadata, memberId: String): Unit = {
    group.remove(memberId)

    if (group.is(PreparingRebalance)) {
      rebalancePurgatory.checkAndComplete(GroupJoinKey(group.groupId))
    }
  }

  def tryCompleteJoin(group: GroupMetadata, forceComplete: () => Boolean): Boolean = {
    group.inLock {
      if (group.hasAllMembersJoined)
        forceComplete()
      else false
    }
  }

  def onCompleteJoin(group: GroupMetadata): Unit = {
    group.inLock {
      val notYetRejoinedDynamicMembers = group.notYetRejoinedMembers.filterNot(_._2.isStaticMember)
      if (notYetRejoinedDynamicMembers.nonEmpty) {
        info(s"Group ${group.groupId} removed dynamic members " +
          s"who haven't joined: ${notYetRejoinedDynamicMembers.keySet}")

        notYetRejoinedDynamicMembers.values.foreach { failedMember =>
          group.remove(failedMember.memberId)
          removeHeartbeatForLeavingMember(group, failedMember.memberId)
        }
      }

      if (group.is(Dead)) {
        info(s"Group ${group.groupId} is dead, skipping rebalance stage")
      } else if (!group.maybeElectNewJoinedLeader() && group.allMembers.nonEmpty) {
        // If all members are not rejoining, we will postpone the completion
        // of rebalance preparing stage, and send out another delayed operation
        // until session timeout removes all the non-responsive members.
        error(s"Group ${group.groupId} could not complete rebalance because no members rejoined")
        rebalancePurgatory.tryCompleteElseWatch(
          new DelayedJoin(this, group, group.rebalanceTimeoutMs),
          Seq(GroupJoinKey(group.groupId)))
      } else {
        group.initNextGeneration()
        if (group.is(Empty)) {
          info(s"Group ${group.groupId} with generation ${group.generationId} is now empty " +
            s"(${Topic.GROUP_METADATA_TOPIC_NAME}-${partitionFor(group.groupId)})")

          groupManager.storeGroup(group, Map.empty, error => {
            if (error != Errors.NONE) {
              // we failed to write the empty group metadata. If the broker fails before another rebalance,
              // the previous generation written to the log will become active again (and most likely timeout).
              // This should be safe since there are no active members in an empty generation, so we just warn.
              warn(s"Failed to write empty metadata for group ${group.groupId}: ${error.message}")
            }
          }, RequestLocal.NoCaching)
        } else {
          info(s"Stabilized group ${group.groupId} generation ${group.generationId} " +
            s"(${Topic.GROUP_METADATA_TOPIC_NAME}-${partitionFor(group.groupId)}) with ${group.size} members")

          // trigger the awaiting join group response callback for all the members after rebalancing
          for (member <- group.allMemberMetadata) {
            val joinResult = JoinGroupResult(
              members = if (group.isLeader(member.memberId)) {
                group.currentMemberMetadata
              } else {
                List.empty
              },
              memberId = member.memberId,
              generationId = group.generationId,
              protocolType = group.protocolType,
              protocolName = group.protocolName,
              leaderId = group.leaderOrNull,
              error = Errors.NONE)

            group.maybeInvokeJoinCallback(member, joinResult)
            completeAndScheduleNextHeartbeatExpiration(group, member)
            member.isNew = false

            group.addPendingSyncMember(member.memberId)
          }

          schedulePendingSync(group)
        }
      }
    }
  }

  private def removePendingSyncMember(
    group: GroupMetadata,
    memberId: String
  ): Unit = {
    group.removePendingSyncMember(memberId)
    maybeCompleteSyncExpiration(group)
  }

  private def removeSyncExpiration(
    group: GroupMetadata
  ): Unit = {
    group.clearPendingSyncMembers()
    maybeCompleteSyncExpiration(group)
  }

  private def maybeCompleteSyncExpiration(
    group: GroupMetadata
  ): Unit = {
    val groupKey = GroupSyncKey(group.groupId)
    rebalancePurgatory.checkAndComplete(groupKey)
  }

  private def schedulePendingSync(
    group: GroupMetadata
  ): Unit = {
    val delayedSync = new DelayedSync(this, group, group.generationId, group.rebalanceTimeoutMs)
    val groupKey = GroupSyncKey(group.groupId)
    rebalancePurgatory.tryCompleteElseWatch(delayedSync, Seq(groupKey))
  }

  def tryCompletePendingSync(
    group: GroupMetadata,
    generationId: Int,
    forceComplete: () => Boolean
  ): Boolean = {
    group.inLock {
      if (generationId != group.generationId) {
        forceComplete()
      } else {
        group.currentState match {
          case Dead | Empty | PreparingRebalance =>
            forceComplete()
          case CompletingRebalance | Stable =>
            if (group.hasReceivedSyncFromAllMembers)
              forceComplete()
            else false
        }
      }
    }
  }

  def onExpirePendingSync(
    group: GroupMetadata,
    generationId: Int
  ): Unit = {
    group.inLock {
      if (generationId != group.generationId) {
        error(s"Received unexpected notification of sync expiration for ${group.groupId} " +
          s"with an old generation $generationId while the group has ${group.generationId}.")
      } else {
        group.currentState match {
          case Dead | Empty | PreparingRebalance =>
            error(s"Received unexpected notification of sync expiration after group ${group.groupId} " +
              s"already transitioned to the ${group.currentState} state.")

          case CompletingRebalance | Stable =>
            if (!group.hasReceivedSyncFromAllMembers) {
              val pendingSyncMembers = group.allPendingSyncMembers

              pendingSyncMembers.foreach { memberId =>
                group.remove(memberId)
                removeHeartbeatForLeavingMember(group, memberId)
              }

              debug(s"Group ${group.groupId} removed members who haven't " +
                s"sent their sync request: $pendingSyncMembers")

              prepareRebalance(group, s"Removing $pendingSyncMembers on pending sync request expiration")
            }
        }
      }
    }
  }
  // 组协调器判断一次心跳检测是否成功，满足下面三个标准的其中之一即可
  def tryCompleteHeartbeat(group: GroupMetadata,
                           memberId: String,
                           isPending: Boolean,
                           forceComplete: () => Boolean): Boolean = {
    group.inLock {
      // The group has been unloaded and invalid, we should complete the heartbeat.
      // 1.如果消费组状态已经是Dead了
      if (group.is(Dead)) {
        forceComplete()
      // 2.如果消费组状态为Pending -> 说明在重平衡中
      } else if (isPending) {
        // complete the heartbeat if the member has joined the group
        if (group.has(memberId)) {
          forceComplete()
        } else false
      // 3.hasSatisfiedHeartbeat为true，说明收到一个有效的心跳包
      } else if (shouldCompleteNonPendingHeartbeat(group, memberId)) {
        forceComplete()
      } else false
    }
  }

  def shouldCompleteNonPendingHeartbeat(group: GroupMetadata, memberId: String): Boolean = {
    if (group.has(memberId)) {
      val member = group.get(memberId)
      member.hasSatisfiedHeartbeat
    } else {
      debug(s"Member id $memberId was not found in ${group.groupId} during heartbeat completion check")
      true
    }
  }

  def onExpireHeartbeat(group: GroupMetadata, memberId: String, isPending: Boolean): Unit = {
    group.inLock {
      if (group.is(Dead)) {
        info(s"Received notification of heartbeat expiration for member $memberId after group ${group.groupId} had already been unloaded or deleted.")
      } else if (isPending) {
        info(s"Pending member $memberId in group ${group.groupId} has been removed after session timeout expiration.")
        removePendingMemberAndUpdateGroup(group, memberId)
      } else if (!group.has(memberId)) {
        debug(s"Member $memberId has already been removed from the group.")
      } else {
        val member = group.get(memberId)
        if (!member.hasSatisfiedHeartbeat) {
          info(s"Member ${member.memberId} in group ${group.groupId} has failed, removing it from the group")
          removeMemberAndUpdateGroup(group, member, s"removing member ${member.memberId} on heartbeat expiration")
        }
      }
    }
  }

  def partitionFor(group: String): Int = groupManager.partitionFor(group)

  private def groupIsOverCapacity(group: GroupMetadata): Boolean = {
    group.size > groupConfig.groupMaxSize
  }

  private def isCoordinatorForGroup(groupId: String) = groupManager.isGroupLocal(groupId)

  private def isCoordinatorLoadInProgress(groupId: String) = groupManager.isGroupLoading(groupId)
}

object GroupCoordinator {

  val NoState = ""
  val NoProtocolType = ""
  val NoProtocol = ""
  val NoLeader = ""
  val NoGeneration = -1
  val NoMembers = List[MemberSummary]()
  val EmptyGroup = GroupSummary(NoState, NoProtocolType, NoProtocol, NoMembers)
  val DeadGroup = GroupSummary(Dead.toString, NoProtocolType, NoProtocol, NoMembers)
  val NewMemberJoinTimeoutMs: Int = 5 * 60 * 1000

  def apply(config: KafkaConfig,
            replicaManager: ReplicaManager,
            time: Time,
            metrics: Metrics): GroupCoordinator = {
    val heartbeatPurgatory = DelayedOperationPurgatory[DelayedHeartbeat]("Heartbeat", config.brokerId)
    val rebalancePurgatory = DelayedOperationPurgatory[DelayedRebalance]("Rebalance", config.brokerId)
    GroupCoordinator(config, replicaManager, heartbeatPurgatory, rebalancePurgatory, time, metrics)
  }

  private[group] def offsetConfig(config: KafkaConfig) = OffsetConfig(
    maxMetadataSize = config.offsetMetadataMaxSize,
    loadBufferSize = config.offsetsLoadBufferSize,
    offsetsRetentionMs = config.offsetsRetentionMinutes * 60L * 1000L,
    offsetsRetentionCheckIntervalMs = config.offsetsRetentionCheckIntervalMs,
    offsetsTopicNumPartitions = config.offsetsTopicPartitions,
    offsetsTopicSegmentBytes = config.offsetsTopicSegmentBytes,
    offsetsTopicReplicationFactor = config.offsetsTopicReplicationFactor,
    offsetsTopicCompressionCodec = config.offsetsTopicCompressionCodec,
    offsetCommitTimeoutMs = config.offsetCommitTimeoutMs,
    offsetCommitRequiredAcks = config.offsetCommitRequiredAcks
  )

  def apply(config: KafkaConfig,
            replicaManager: ReplicaManager,
            heartbeatPurgatory: DelayedOperationPurgatory[DelayedHeartbeat],
            rebalancePurgatory: DelayedOperationPurgatory[DelayedRebalance],
            time: Time,
            metrics: Metrics): GroupCoordinator = {
    val offsetConfig = this.offsetConfig(config)
    val groupConfig = GroupConfig(groupMinSessionTimeoutMs = config.groupMinSessionTimeoutMs,
      groupMaxSessionTimeoutMs = config.groupMaxSessionTimeoutMs,
      groupMaxSize = config.groupMaxSize,
      groupInitialRebalanceDelayMs = config.groupInitialRebalanceDelay)

    val groupMetadataManager = new GroupMetadataManager(config.brokerId, config.interBrokerProtocolVersion,
      offsetConfig, replicaManager, time, metrics)
    new GroupCoordinator(config.brokerId, groupConfig, offsetConfig, groupMetadataManager, heartbeatPurgatory,
      rebalancePurgatory, time, metrics)
  }

  private def memberLeaveError(memberIdentity: MemberIdentity,
                               error: Errors): LeaveMemberResponse = {
    LeaveMemberResponse(
      memberId = memberIdentity.memberId,
      groupInstanceId = Option(memberIdentity.groupInstanceId),
      error = error)
  }

  private def leaveError(topLevelError: Errors,
                         memberResponses: List[LeaveMemberResponse]): LeaveGroupResult = {
    LeaveGroupResult(
      topLevelError = topLevelError,
      memberResponses = memberResponses)
  }
}

case class GroupConfig(groupMinSessionTimeoutMs: Int,
                       groupMaxSessionTimeoutMs: Int,
                       groupMaxSize: Int,
                       groupInitialRebalanceDelayMs: Int)

case class JoinGroupResult(members: List[JoinGroupResponseMember],
                           memberId: String,
                           generationId: Int,
                           protocolType: Option[String],
                           protocolName: Option[String],
                           leaderId: String,
                           error: Errors)

object JoinGroupResult {
  def apply(memberId: String, error: Errors): JoinGroupResult = {
    JoinGroupResult(
      members = List.empty,
      memberId = memberId,
      generationId = GroupCoordinator.NoGeneration,
      protocolType = None,
      protocolName = None,
      leaderId = GroupCoordinator.NoLeader,
      error = error)
  }
}

case class SyncGroupResult(protocolType: Option[String],
                           protocolName: Option[String],
                           memberAssignment: Array[Byte],
                           error: Errors)

object SyncGroupResult {
  def apply(error: Errors): SyncGroupResult = {
    SyncGroupResult(None, None, Array.empty, error)
  }
}

case class LeaveMemberResponse(memberId: String,
                               groupInstanceId: Option[String],
                               error: Errors)

case class LeaveGroupResult(topLevelError: Errors,
                            memberResponses : List[LeaveMemberResponse])
