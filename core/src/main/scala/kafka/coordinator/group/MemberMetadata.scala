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

import java.util

import kafka.utils.nonthreadsafe

/**
 * 组成员元数据的概要数据类
 * @param memberId 消费者组成员的 ID，由 Kafka 自动生成
 * @param groupInstanceId 消费者组静态成员的 ID
 * @param clientId 消费者组成员配置的 client.id 参数
 * @param clientHost 运行消费者程序的主机名
 * @param metadata 标识消费者组成员分区分配策略的字节数组，由消费者端参数 partition.assignment.strategy 值设定
 * @param assignment 保存分配给该成员的分区分配方案
 */
case class MemberSummary(memberId: String,
                         groupInstanceId: Option[String],
                         clientId: String,
                         clientHost: String,
                         metadata: Array[Byte],
                         assignment: Array[Byte])

/**
 * MemberMetadata 伴生对象
 */
private object MemberMetadata {
  /**
   * 提取分区分配策略集合，常用来统计一个消费者组下的成员到底配置了多少种分区分配策略
   * @param supportedProtocols
   * @return
   */
  def plainProtocolSet(supportedProtocols: List[(String, Array[Byte])]) = supportedProtocols.map(_._1).toSet
}

/**
 * Member metadata contains the following metadata:
 *
 * Heartbeat metadata:
 * 1. negotiated heartbeat session timeout
 * 2. timestamp of the latest heartbeat
 *
 * Protocol metadata:
 * 1. the list of supported protocols (ordered by preference)
 * 2. the metadata associated with each protocol
 *
 * In addition, it also contains the following state information:
 *
 * 1. Awaiting rebalance callback: when the group is in the prepare-rebalance state,
 *                                 its rebalance callback will be kept in the metadata if the
 *                                 member has sent the join group request
 * 2. Awaiting sync callback: when the group is in the awaiting-sync state, its sync callback
 *                            is kept in metadata until the leader provides the group assignment
 *                            and the group transitions to stable
 * 
 * @param memberId 消费者组成员的 ID，由 Kafka 自动生成
 * @param groupInstanceId 消费者组静态成员的 ID
 * @param clientId 消费者组成员配置的 client.id 参数
 * @param clientHost 运行消费者程序的主机名
 * @param rebalanceTimeoutMs Rebalance 操作的超时时间，由 Consumer 端参数 max.poll.interval.ms 指定
 * @param sessionTimeoutMs 会话超时时间，由 Consumer 端参数 session.timeout.ms 指定
 * @param protocolType 协议类型，标识了消费者组被用在了哪个场景
 * @param supportedProtocols 标识成员配置的多组分区分配策略
 * @param assignment 保存分配给该成员的分区分配方案
 */
@nonthreadsafe
private[group] class MemberMetadata(var memberId: String,
                                    val groupInstanceId: Option[String],
                                    val clientId: String,
                                    val clientHost: String,
                                    val rebalanceTimeoutMs: Int,
                                    val sessionTimeoutMs: Int,
                                    val protocolType: String,
                                    var supportedProtocols: List[(String, Array[Byte])],
                                    var assignment: Array[Byte] = Array.empty[Byte]) {

  /**
   * 表示组成员是否正在等待加入组
   */
  var awaitingJoinCallback: JoinGroupResult => Unit = _
  /**
   * 表示组成员是否正在等待 GroupCoordinator 发送分配方案
   */
  var awaitingSyncCallback: SyncGroupResult => Unit = _
  /**
   * 表示是否是消费者组下的新成员
   */
  var isNew: Boolean = false

  def isStaticMember: Boolean = groupInstanceId.isDefined

  // This variable is used to track heartbeat completion through the delayed
  // heartbeat purgatory. When scheduling a new heartbeat expiration, we set
  // this value to `false`. Upon receiving the heartbeat (or any other event
  // indicating the liveness of the client), we set it to `true` so that the
  // delayed heartbeat can be completed.
  var heartbeatSatisfied: Boolean = false

  def isAwaitingJoin: Boolean = awaitingJoinCallback != null
  def isAwaitingSync: Boolean = awaitingSyncCallback != null

  /**
   * Get metadata corresponding to the provided protocol.
   */
  def metadata(protocol: String): Array[Byte] = {
    // 从配置的分区分配策略中寻找给定策略
    supportedProtocols.find(_._1 == protocol) match {
      case Some((_, metadata)) => metadata
      case None =>
        throw new IllegalArgumentException("Member does not support protocol")
    }
  }

  def hasSatisfiedHeartbeat: Boolean = {
    if (isNew) {
      // New members can be expired while awaiting join, so we have to check this first
      heartbeatSatisfied
    } else if (isAwaitingJoin || isAwaitingSync) {
      // Members that are awaiting a rebalance automatically satisfy expected heartbeats
      true
    } else {
      // Otherwise we require the next heartbeat
      heartbeatSatisfied
    }
  }

  /**
   * Check if the provided protocol metadata matches the currently stored metadata.
   */
  def matches(protocols: List[(String, Array[Byte])]): Boolean = {
    if (protocols.size != this.supportedProtocols.size)
      return false

    for (i <- protocols.indices) {
      val p1 = protocols(i)
      val p2 = supportedProtocols(i)
      if (p1._1 != p2._1 || !util.Arrays.equals(p1._2, p2._2))
        return false
    }
    true
  }

  def summary(protocol: String): MemberSummary = {
    MemberSummary(memberId, groupInstanceId, clientId, clientHost, metadata(protocol), assignment)
  }

  def summaryNoMetadata(): MemberSummary = {
    MemberSummary(memberId, groupInstanceId, clientId, clientHost, Array.empty[Byte], Array.empty[Byte])
  }

  /**
   * Vote for one of the potential group protocols. This takes into account the protocol preference as
   * indicated by the order of supported protocols and returns the first one also contained in the set
   * 
   * 为分区分配策略投票
   * 
   * @param candidates
   * @return
   */  
  def vote(candidates: Set[String]): String = {
    // 找出成员支持策略列表中，第一个包含在 candidates 中的策略返回
    supportedProtocols.find({ case (protocol, _) => candidates.contains(protocol)}) match {
      case Some((protocol, _)) => protocol
      case None =>
        throw new IllegalArgumentException("Member does not support any of the candidate protocols")
    }
  }

  override def toString: String = {
    "MemberMetadata(" +
      s"memberId=$memberId, " +
      s"groupInstanceId=$groupInstanceId, " +
      s"clientId=$clientId, " +
      s"clientHost=$clientHost, " +
      s"sessionTimeoutMs=$sessionTimeoutMs, " +
      s"rebalanceTimeoutMs=$rebalanceTimeoutMs, " +
      s"supportedProtocols=${supportedProtocols.map(_._1)}" +
      ")"
  }
}
