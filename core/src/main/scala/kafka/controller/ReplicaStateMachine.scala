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
package kafka.controller

import kafka.api.LeaderAndIsr
import kafka.common.StateChangeFailedException
import kafka.server.KafkaConfig
import kafka.utils.Implicits._
import kafka.utils.Logging
import kafka.zk.KafkaZkClient
import kafka.zk.KafkaZkClient.UpdateLeaderAndIsrResult
import kafka.zk.TopicPartitionStateZNode
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.ControllerMovedException
import org.apache.zookeeper.KeeperException.Code
import scala.collection.{Seq, mutable}

/**
 * 副本状态机抽象类，定义状态机启停与状态管理相关方法
 * @param controllerContext
 */
abstract class ReplicaStateMachine(controllerContext: ControllerContext) extends Logging {
  /**
   * Invoked on successful controller election.
   */
  def startup(): Unit = {
    info("Initializing replica state")
    initializeReplicaState()
    info("Triggering online replica state changes")
    val (onlineReplicas, offlineReplicas) = controllerContext.onlineAndOfflineReplicas
    handleStateChanges(onlineReplicas.toSeq, OnlineReplica)
    info("Triggering offline replica state changes")
    handleStateChanges(offlineReplicas.toSeq, OfflineReplica)
    debug(s"Started replica state machine with initial state -> ${controllerContext.replicaStates}")
  }

  /**
   * Invoked on controller shutdown.
   */
  def shutdown(): Unit = {
    info("Stopped replica state machine")
  }

  /**
   * Invoked on startup of the replica's state machine to set the initial state for replicas of all existing partitions
   * in zookeeper
   */
  private def initializeReplicaState(): Unit = {
    controllerContext.allPartitions.foreach { partition =>
      val replicas = controllerContext.partitionReplicaAssignment(partition)
      replicas.foreach { replicaId =>
        val partitionAndReplica = PartitionAndReplica(partition, replicaId)
        if (controllerContext.isReplicaOnline(replicaId, partition)) {
          controllerContext.putReplicaState(partitionAndReplica, OnlineReplica)
        } else {
          // mark replicas on dead brokers as failed for topic deletion, if they belong to a topic to be deleted.
          // This is required during controller failover since during controller failover a broker can go down,
          // so the replicas on that broker should be moved to ReplicaDeletionIneligible to be on the safer side.
          controllerContext.putReplicaState(partitionAndReplica, ReplicaDeletionIneligible)
        }
      }
    }
  }

  def handleStateChanges(replicas: Seq[PartitionAndReplica], targetState: ReplicaState): Unit
}

/**
 * 
 * This class represents the state machine for replicas. It defines the states that a replica can be in, and
 * transitions to move the replica to another legal state. The different states that a replica can be in are -
 * 1. NewReplica        : The controller can create new replicas during partition reassignment. In this state, a
 *                        replica can only get become follower state change request.  Valid previous
 *                        state is NonExistentReplica
 * 2. OnlineReplica     : Once a replica is started and part of the assigned replicas for its partition, it is in this
 *                        state. In this state, it can get either become leader or become follower state change requests.
 *                        Valid previous state are NewReplica, OnlineReplica, OfflineReplica and ReplicaDeletionIneligible
 * 3. OfflineReplica    : If a replica dies, it moves to this state. This happens when the broker hosting the replica
 *                        is down. Valid previous state are NewReplica, OnlineReplica, OfflineReplica and ReplicaDeletionIneligible
 * 4. ReplicaDeletionStarted: If replica deletion starts, it is moved to this state. Valid previous state is OfflineReplica
 * 5. ReplicaDeletionSuccessful: If replica responds with no error code in response to a delete replica request, it is
 *                        moved to this state. Valid previous state is ReplicaDeletionStarted
 * 6. ReplicaDeletionIneligible: If replica deletion fails, it is moved to this state. Valid previous states are
 *                        ReplicaDeletionStarted and OfflineReplica
 * 7. NonExistentReplica: If a replica is deleted successfully, it is moved to this state. Valid previous state is
 *                        ReplicaDeletionSuccessful
 * 
 * 副本状态机的具体且唯一实现类
 * @param config
 * @param stateChangeLogger
 * @param controllerContext
 * @param zkClient
 * @param controllerBrokerRequestBatch
 */
class ZkReplicaStateMachine(config: KafkaConfig,
                            stateChangeLogger: StateChangeLogger,
                            controllerContext: ControllerContext,
                            zkClient: KafkaZkClient,
                            controllerBrokerRequestBatch: ControllerBrokerRequestBatch)
  extends ReplicaStateMachine(controllerContext) with Logging {

  private val controllerId = config.brokerId
  this.logIdent = s"[ReplicaStateMachine controllerId=$controllerId] "

  /**
   * 处理副本状态变更的入口方法
   * @param replicas
   * @param targetState
   */
  override def handleStateChanges(replicas: Seq[PartitionAndReplica], targetState: ReplicaState): Unit = {
    if (replicas.nonEmpty) {
      try {
        // 清空 Controller 待发送请求集合
        controllerBrokerRequestBatch.newBatch()
        // 将副本按照 Broker 进行分组，依次执行状态转换操作
        replicas.groupBy(_.replica).forKeyValue { (replicaId, replicas) =>
          // 具体的状态转换逻辑
          doHandleStateChanges(replicaId, replicas, targetState)
        }
        // 给 Broker 发送相应的 Controller 请求
        controllerBrokerRequestBatch.sendRequestsToBrokers(controllerContext.epoch)
      } catch {
        // 如果 Controller 发生变化，则记录错误日志并直接抛异常
        case e: ControllerMovedException =>
          error(s"Controller moved to another broker when moving some replicas to $targetState state", e)
          throw e
        case e: Throwable => error(s"Error while moving some replicas to $targetState state", e)
      }
    }
  }

  /**
   * This API exercises the replica's state machine. It ensures that every state transition happens from a legal
   * previous state to the target state. Valid state transitions are:
   * NonExistentReplica --> NewReplica
   * --send LeaderAndIsr request with current leader and isr to the new replica and UpdateMetadata request for the
   *   partition to every live broker
   *
   * NewReplica -> OnlineReplica
   * --add the new replica to the assigned replica list if needed
   *
   * OnlineReplica,OfflineReplica -> OnlineReplica
   * --send LeaderAndIsr request with current leader and isr to the new replica and UpdateMetadata request for the
   *   partition to every live broker
   *
   * NewReplica,OnlineReplica,OfflineReplica,ReplicaDeletionIneligible -> OfflineReplica
   * --send StopReplicaRequest to the replica (w/o deletion)
   * --remove this replica from the isr and send LeaderAndIsr request (with new isr) to the leader replica and
   *   UpdateMetadata request for the partition to every live broker.
   *
   * OfflineReplica -> ReplicaDeletionStarted
   * --send StopReplicaRequest to the replica (with deletion)
   *
   * ReplicaDeletionStarted -> ReplicaDeletionSuccessful
   * -- mark the state of the replica in the state machine
   *
   * ReplicaDeletionStarted -> ReplicaDeletionIneligible
   * -- mark the state of the replica in the state machine
   *
   * ReplicaDeletionSuccessful -> NonExistentReplica
   * -- remove the replica from the in memory partition replica assignment cache
   *
   * @param replicaId The replica for which the state transition is invoked
   * @param replicas The partitions on this replica for which the state transition is invoked
   * @param targetState The end state that the replica should be moved to
   *                    
   *                    
   * 执行副本状态转换的具体逻辑                   
   */
  private def doHandleStateChanges(replicaId: Int, replicas: Seq[PartitionAndReplica], targetState: ReplicaState): Unit = {
    val stateLogger = stateChangeLogger.withControllerEpoch(controllerContext.epoch)
    val traceEnabled = stateLogger.isTraceEnabled
    replicas.foreach(replica => controllerContext.putReplicaStateIfNotExists(replica, NonExistentReplica))
    val (validReplicas, invalidReplicas) = controllerContext.checkValidReplicaStateChange(replicas, targetState)
    invalidReplicas.foreach(replica => logInvalidTransition(replica, targetState))

    // 模式匹配目标状态，执行转换到目标状态的对应具体逻辑
    targetState match {
      // 转移到副本被创建的状态
      case NewReplica =>
        // 遍历所有能执行转换操作的副本对象
        validReplicas.foreach { replica =>
          // 获取副本对应的分区对象，即 <主题名, 分区号>
          val partition = replica.topicPartition
          // 获取副本当前状态
          val currentState = controllerContext.replicaState(replica)

          // 从 Controller 元数据缓存中获取分区当前信息
          controllerContext.partitionLeadershipInfo(partition) match {
            // 成功拿到分区信息
            case Some(leaderIsrAndControllerEpoch) =>
              // 如果当前副本是 Leader 副本
              if (leaderIsrAndControllerEpoch.leaderAndIsr.leader == replicaId) {
                val exception = new StateChangeFailedException(s"Replica $replicaId for partition $partition cannot be moved to NewReplica state as it is being requested to become leader")
                // 则需记录错误日志，因为 Leader 副本不能被设置为 NewReplica 状态
                logFailedStateChange(replica, currentState, OfflineReplica, exception)
              // 不是 Leader 副本，给该副本所在的 Broker 发送  LeaderAndIsrRequest 请求，向其同步该分区数据，
              // 之后给集群当前所有 Broker 发送 UpdateMetadataRequest 请求，通知它们该分区数据发生变更
              } else {
                controllerBrokerRequestBatch.addLeaderAndIsrRequestForBrokers(Seq(replicaId),
                  replica.topicPartition,
                  leaderIsrAndControllerEpoch,
                  controllerContext.partitionFullReplicaAssignment(replica.topicPartition),
                  isNew = true)
                if (traceEnabled)
                  logSuccessfulTransition(stateLogger, replicaId, partition, currentState, NewReplica)
                // 更新元数据缓存中该副本对象的当前状态为 NewReplica
                controllerContext.putReplicaState(replica, NewReplica)
              }
            // 找不到分区信息相关数据，只需更新元数据缓存中该副本对象的当前状态为 NewReplica
            case None =>
              if (traceEnabled)
                logSuccessfulTransition(stateLogger, replicaId, partition, currentState, NewReplica)
              controllerContext.putReplicaState(replica, NewReplica)
          }
        }
      // 转移到副本正常工作状态  
      case OnlineReplica =>
        validReplicas.foreach { replica =>
          // 获取分区信息
          val partition = replica.topicPartition
          // 获取副本当前状态
          val currentState = controllerContext.replicaState(replica)

          currentState match {
            // 当前是 NewReplica 状态
            case NewReplica =>
              // 从 Controller 元数据缓存中获取分区副本列表信息
              val assignment = controllerContext.partitionFullReplicaAssignment(partition)
              // 副本列表中没有当前副本信息，视为异常情况
              if (!assignment.replicas.contains(replicaId)) {
                error(s"Adding replica ($replicaId) that is not part of the assignment $assignment")
                // 将当前副本加入到副本列表中，并更新元数据缓存中该分区的副本列表
                val newAssignment = assignment.copy(replicas = assignment.replicas :+ replicaId)
                controllerContext.updatePartitionFullReplicaAssignment(partition, newAssignment)
              }
            // 当前是除 NewReplica 外的其他状态  
            case _ =>
              // 从元数据缓存中获取当前分区相关信息
              controllerContext.partitionLeadershipInfo(partition) match {
                // 如果存在分区信息，则向该副本对象所在 Broker 发送请求，令其同步该分区数据
                case Some(leaderIsrAndControllerEpoch) =>
                  controllerBrokerRequestBatch.addLeaderAndIsrRequestForBrokers(Seq(replicaId),
                    replica.topicPartition,
                    leaderIsrAndControllerEpoch,
                    controllerContext.partitionFullReplicaAssignment(partition), isNew = false)
                case None =>
              }
          }
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, partition, currentState, OnlineReplica)
          // 将副本对象状态设置为 OnlineReplica  
          controllerContext.putReplicaState(replica, OnlineReplica)
        }
      // 转换到副本下线状态  
      case OfflineReplica =>
        // 向副本所在 Broker 发送 StopReplicaRequest 请求，停止相应副本提供服务
        validReplicas.foreach { replica =>
          controllerBrokerRequestBatch.addStopReplicaRequestForBrokers(Seq(replicaId), replica.topicPartition, deletePartition = false)
        }
        // 将副本集合划分为「有 Leader 信息的副本集合」 和 「没有 Leader 信息的副本集合」
        val (replicasWithLeadershipInfo, replicasWithoutLeadershipInfo) = validReplicas.partition { replica =>
          controllerContext.partitionLeadershipInfo(replica.topicPartition).isDefined
        }
        // 有 Leader 信息的副本集合，将他们从对应的所有分区中移除并更新 ZK 节点
        val updatedLeaderIsrAndControllerEpochs = removeReplicasFromIsr(replicaId, replicasWithLeadershipInfo.map(_.topicPartition))
        // 遍历在上一步每个更新过的分区信息
        updatedLeaderIsrAndControllerEpochs.forKeyValue { (partition, leaderIsrAndControllerEpoch) =>
          stateLogger.info(s"Partition $partition state changed to $leaderIsrAndControllerEpoch after removing replica $replicaId from the ISR as part of transition to $OfflineReplica")
          // 如果分区对应 Topic 并没有被删除
          if (!controllerContext.isTopicQueuedUpForDeletion(partition.topic)) {
            // 获取该分区的，给定副本外的，其他副本所在的 Broker
            val recipients = controllerContext.partitionReplicaAssignment(partition).filterNot(_ == replicaId)
            // 向这些 Broker 发送请求更新该分区更新过的分区 LeaderAndIsr 数据，保证数据是一致的
            controllerBrokerRequestBatch.addLeaderAndIsrRequestForBrokers(recipients,
              partition,
              leaderIsrAndControllerEpoch,
              controllerContext.partitionFullReplicaAssignment(partition), isNew = false)
          }
          val replica = PartitionAndReplica(partition, replicaId)
          val currentState = controllerContext.replicaState(replica)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, partition, currentState, OfflineReplica)
          // 将副本状态转换为 OfflineReplica
          controllerContext.putReplicaState(replica, OfflineReplica)
        }

        // 遍历无 Leader 信息的所有副本对象
        replicasWithoutLeadershipInfo.foreach { replica =>
          val currentState = controllerContext.replicaState(replica)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, replica.topicPartition, currentState, OfflineReplica)
          // 向集群所有 Broker 发送请求，更新对应分区的元数据  
          controllerBrokerRequestBatch.addUpdateMetadataRequestForBrokers(controllerContext.liveOrShuttingDownBrokerIds.toSeq, Set(replica.topicPartition))
          // 将副本状态转换为 OfflineReplica
          controllerContext.putReplicaState(replica, OfflineReplica)
        }
      case ReplicaDeletionStarted =>
        validReplicas.foreach { replica =>
          val currentState = controllerContext.replicaState(replica)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, replica.topicPartition, currentState, ReplicaDeletionStarted)
          controllerContext.putReplicaState(replica, ReplicaDeletionStarted)
          controllerBrokerRequestBatch.addStopReplicaRequestForBrokers(Seq(replicaId), replica.topicPartition, deletePartition = true)
        }
      case ReplicaDeletionIneligible =>
        validReplicas.foreach { replica =>
          val currentState = controllerContext.replicaState(replica)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, replica.topicPartition, currentState, ReplicaDeletionIneligible)
          controllerContext.putReplicaState(replica, ReplicaDeletionIneligible)
        }
      case ReplicaDeletionSuccessful =>
        validReplicas.foreach { replica =>
          val currentState = controllerContext.replicaState(replica)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, replica.topicPartition, currentState, ReplicaDeletionSuccessful)
          controllerContext.putReplicaState(replica, ReplicaDeletionSuccessful)
        }
      case NonExistentReplica =>
        validReplicas.foreach { replica =>
          val currentState = controllerContext.replicaState(replica)
          val newAssignedReplicas = controllerContext
            .partitionFullReplicaAssignment(replica.topicPartition)
            .removeReplica(replica.replica)

          controllerContext.updatePartitionFullReplicaAssignment(replica.topicPartition, newAssignedReplicas)
          if (traceEnabled)
            logSuccessfulTransition(stateLogger, replicaId, replica.topicPartition, currentState, NonExistentReplica)
          controllerContext.removeReplicaState(replica)
        }
    }
  }

  /**
   * Repeatedly attempt to remove a replica from the isr of multiple partitions until there are no more remaining partitions
   * to retry.
   * @param replicaId The replica being removed from isr of multiple partitions
   * @param partitions The partitions from which we're trying to remove the replica from isr
   * @return The updated LeaderIsrAndControllerEpochs of all partitions for which we successfully removed the replica from isr.
   */
  private def removeReplicasFromIsr(
    replicaId: Int,
    partitions: Seq[TopicPartition]
  ): Map[TopicPartition, LeaderIsrAndControllerEpoch] = {
    var results = Map.empty[TopicPartition, LeaderIsrAndControllerEpoch]
    var remaining = partitions
    while (remaining.nonEmpty) {
      val (finishedRemoval, removalsToRetry) = doRemoveReplicasFromIsr(replicaId, remaining)
      remaining = removalsToRetry

      finishedRemoval.foreach {
        case (partition, Left(e)) =>
            val replica = PartitionAndReplica(partition, replicaId)
            val currentState = controllerContext.replicaState(replica)
            logFailedStateChange(replica, currentState, OfflineReplica, e)
        case (partition, Right(leaderIsrAndEpoch)) =>
          results += partition -> leaderIsrAndEpoch
      }
    }
    results
  }

  /**
   * Try to remove a replica from the isr of multiple partitions.
   * Removing a replica from isr updates partition state in zookeeper.
   *
   * @param replicaId The replica being removed from isr of multiple partitions
   * @param partitions The partitions from which we're trying to remove the replica from isr
   * @return A tuple of two elements:
   *         1. The updated Right[LeaderIsrAndControllerEpochs] of all partitions for which we successfully
   *         removed the replica from isr. Or Left[Exception] corresponding to failed removals that should
   *         not be retried
   *         2. The partitions that we should retry due to a zookeeper BADVERSION conflict. Version conflicts can occur if
   *         the partition leader updated partition state while the controller attempted to update partition state.
   *         
   * 将指定副本从指定分区中移除        
   */
  private def doRemoveReplicasFromIsr(
    replicaId: Int,
    partitions: Seq[TopicPartition]
  ): (Map[TopicPartition, Either[Exception, LeaderIsrAndControllerEpoch]], Seq[TopicPartition]) = {
    val (leaderAndIsrs, partitionsWithNoLeaderAndIsrInZk) = getTopicPartitionStatesFromZk(partitions)
    val (leaderAndIsrsWithReplica, leaderAndIsrsWithoutReplica) = leaderAndIsrs.partition { case (_, result) =>
      result.map { leaderAndIsr =>
        leaderAndIsr.isr.contains(replicaId)
      }.getOrElse(false)
    }

    val adjustedLeaderAndIsrs: Map[TopicPartition, LeaderAndIsr] = leaderAndIsrsWithReplica.flatMap {
      case (partition, result) =>
        result.toOption.map { leaderAndIsr =>
          val newLeader = if (replicaId == leaderAndIsr.leader) LeaderAndIsr.NoLeader else leaderAndIsr.leader
          val adjustedIsr = if (leaderAndIsr.isr.size == 1) leaderAndIsr.isr else leaderAndIsr.isr.filter(_ != replicaId)
          partition -> leaderAndIsr.newLeaderAndIsr(newLeader, adjustedIsr)
        }
    }

    val UpdateLeaderAndIsrResult(finishedPartitions, updatesToRetry) = zkClient.updateLeaderAndIsr(
      adjustedLeaderAndIsrs, controllerContext.epoch, controllerContext.epochZkVersion)

    val exceptionsForPartitionsWithNoLeaderAndIsrInZk: Map[TopicPartition, Either[Exception, LeaderIsrAndControllerEpoch]] =
      partitionsWithNoLeaderAndIsrInZk.iterator.flatMap { partition =>
        if (!controllerContext.isTopicQueuedUpForDeletion(partition.topic)) {
          val exception = new StateChangeFailedException(
            s"Failed to change state of replica $replicaId for partition $partition since the leader and isr " +
            "path in zookeeper is empty"
          )
          Option(partition -> Left(exception))
        } else None
      }.toMap

    val leaderIsrAndControllerEpochs: Map[TopicPartition, Either[Exception, LeaderIsrAndControllerEpoch]] =
      (leaderAndIsrsWithoutReplica ++ finishedPartitions).map { case (partition, result) =>
        (partition, result.map { leaderAndIsr =>
          val leaderIsrAndControllerEpoch = LeaderIsrAndControllerEpoch(leaderAndIsr, controllerContext.epoch)
          controllerContext.putPartitionLeadershipInfo(partition, leaderIsrAndControllerEpoch)
          leaderIsrAndControllerEpoch
        })
      }

    (leaderIsrAndControllerEpochs ++ exceptionsForPartitionsWithNoLeaderAndIsrInZk, updatesToRetry)
  }

  /**
   * Gets the partition state from zookeeper
   * @param partitions the partitions whose state we want from zookeeper
   * @return A tuple of two values:
   *         1. The Right(LeaderAndIsrs) of partitions whose state we successfully read from zookeeper.
   *         The Left(Exception) to failed zookeeper lookups or states whose controller epoch exceeds our current epoch
   *         2. The partitions that had no leader and isr state in zookeeper. This happens if the controller
   *         didn't finish partition initialization.
   *         
   * 从 ZK 中获取指定分区的状态信息(Leader 副本、ISR 集合等)        
   */
  private def getTopicPartitionStatesFromZk(
    partitions: Seq[TopicPartition]
  ): (Map[TopicPartition, Either[Exception, LeaderAndIsr]], Seq[TopicPartition]) = {
    val getDataResponses = try {
      zkClient.getTopicPartitionStatesRaw(partitions)
    } catch {
      case e: Exception =>
        return (partitions.iterator.map(_ -> Left(e)).toMap, Seq.empty)
    }

    val partitionsWithNoLeaderAndIsrInZk = mutable.Buffer.empty[TopicPartition]
    val result = mutable.Map.empty[TopicPartition, Either[Exception, LeaderAndIsr]]

    getDataResponses.foreach[Unit] { getDataResponse =>
      val partition = getDataResponse.ctx.get.asInstanceOf[TopicPartition]
      if (getDataResponse.resultCode == Code.OK) {
        TopicPartitionStateZNode.decode(getDataResponse.data, getDataResponse.stat) match {
          case None =>
            partitionsWithNoLeaderAndIsrInZk += partition
          case Some(leaderIsrAndControllerEpoch) =>
            if (leaderIsrAndControllerEpoch.controllerEpoch > controllerContext.epoch) {
              val exception = new StateChangeFailedException(
                "Leader and isr path written by another controller. This probably " +
                s"means the current controller with epoch ${controllerContext.epoch} went through a soft failure and " +
                s"another controller was elected with epoch ${leaderIsrAndControllerEpoch.controllerEpoch}. Aborting " +
                "state change by this controller"
              )
              result += (partition -> Left(exception))
            } else {
              result += (partition -> Right(leaderIsrAndControllerEpoch.leaderAndIsr))
            }
        }
      } else if (getDataResponse.resultCode == Code.NONODE) {
        partitionsWithNoLeaderAndIsrInZk += partition
      } else {
        result += (partition -> Left(getDataResponse.resultException.get))
      }
    }

    (result.toMap, partitionsWithNoLeaderAndIsrInZk)
  }

  /**
   * 记录一次成功的状态转换
   * @param logger
   * @param replicaId
   * @param partition
   * @param currState
   * @param targetState
   */
  private def logSuccessfulTransition(logger: StateChangeLogger, replicaId: Int, partition: TopicPartition,
                                      currState: ReplicaState, targetState: ReplicaState): Unit = {
    logger.trace(s"Changed state of replica $replicaId for partition $partition from $currState to $targetState")
  }

  /**
   * 记录一次非法的状态转换
   * @param replica
   * @param targetState
   */
  private def logInvalidTransition(replica: PartitionAndReplica, targetState: ReplicaState): Unit = {
    val currState = controllerContext.replicaState(replica)
    val e = new IllegalStateException(s"Replica $replica should be in the ${targetState.validPreviousStates.mkString(",")} " +
      s"states before moving to $targetState state. Instead it is in $currState state")
    logFailedStateChange(replica, currState, targetState, e)
  }

  /**
   * 执行了一次无效的状态变更，记录一条错误日志
   * @param replica
   * @param currState
   * @param targetState
   * @param t
   */
  private def logFailedStateChange(replica: PartitionAndReplica, currState: ReplicaState, targetState: ReplicaState, t: Throwable): Unit = {
    stateChangeLogger.withControllerEpoch(controllerContext.epoch)
      .error(s"Controller $controllerId epoch ${controllerContext.epoch} initiated state change of replica ${replica.replica} " +
        s"for partition ${replica.topicPartition} from $currState to $targetState failed", t)
  }
}

/**
 * 定义副本状态，目前源码中副本共有7种状态
 */
sealed trait ReplicaState {
  def state: Byte
  def validPreviousStates: Set[ReplicaState]
}

/**
 * 副本创建后的状态
 */
case object NewReplica extends ReplicaState {
  val state: Byte = 1
  val validPreviousStates: Set[ReplicaState] = Set(NonExistentReplica)
}

/**
 * 副本正常提供服务的状态
 */
case object OnlineReplica extends ReplicaState {
  val state: Byte = 2
  val validPreviousStates: Set[ReplicaState] = Set(NewReplica, OnlineReplica, OfflineReplica, ReplicaDeletionIneligible)
}

/**
 * 副本下线的状态
 */
case object OfflineReplica extends ReplicaState {
  val state: Byte = 3
  val validPreviousStates: Set[ReplicaState] = Set(NewReplica, OnlineReplica, OfflineReplica, ReplicaDeletionIneligible)
}

/**
 * 副本删除状态
 */
case object ReplicaDeletionStarted extends ReplicaState {
  val state: Byte = 4
  val validPreviousStates: Set[ReplicaState] = Set(OfflineReplica)
}

/**
 * 副本删除成功的状态
 */
case object ReplicaDeletionSuccessful extends ReplicaState {
  val state: Byte = 5
  val validPreviousStates: Set[ReplicaState] = Set(ReplicaDeletionStarted)
}

/**
 * 副本删除暂停状态(已经开启了副本删除，但是因某些原因暂时无法被删除，比如 Broker 已经下线了)
 */
case object ReplicaDeletionIneligible extends ReplicaState {
  val state: Byte = 6
  val validPreviousStates: Set[ReplicaState] = Set(OfflineReplica, ReplicaDeletionStarted)
}

/**
 * 副本从副本状态机中移除所处的状态
 */
case object NonExistentReplica extends ReplicaState {
  val state: Byte = 7
  val validPreviousStates: Set[ReplicaState] = Set(ReplicaDeletionSuccessful)
}
