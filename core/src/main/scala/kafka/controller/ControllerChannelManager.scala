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

import java.net.SocketTimeoutException
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue, TimeUnit}

import com.yammer.metrics.core.{Gauge, Timer}
import kafka.api._
import kafka.cluster.Broker
import kafka.metrics.KafkaMetricsGroup
import kafka.server.KafkaConfig
import kafka.utils._
import kafka.utils.Implicits._
import org.apache.kafka.clients._
import org.apache.kafka.common.message.LeaderAndIsrRequestData.LeaderAndIsrPartitionState
import org.apache.kafka.common.message.StopReplicaRequestData.{StopReplicaPartitionState, StopReplicaTopicState}
import org.apache.kafka.common.metrics.Metrics
import org.apache.kafka.common.network._
import org.apache.kafka.common.protocol.{ApiKeys, Errors}
import org.apache.kafka.common.message.UpdateMetadataRequestData.{UpdateMetadataBroker, UpdateMetadataEndpoint, UpdateMetadataPartitionState}
import org.apache.kafka.common.requests._
import org.apache.kafka.common.security.JaasContext
import org.apache.kafka.common.security.auth.SecurityProtocol
import org.apache.kafka.common.utils.{LogContext, Time}
import org.apache.kafka.common.{KafkaException, Node, Reconfigurable, TopicPartition, Uuid}

import scala.jdk.CollectionConverters._
import scala.collection.mutable.HashMap
import scala.collection.{Seq, Set, mutable}

object ControllerChannelManager {
  val QueueSizeMetricName = "QueueSize"
  val RequestRateAndQueueTimeMetricName = "RequestRateAndQueueTimeMs"
}

// ControllerChannelManager 类负责：
// 1) 管理 Controller 与集群 Broker 之间的连接，并为每个 Broker 创建 RequestSendThread 线程实例
// 2) 将待发送的请求放入对应 Broker 的阻塞队列中，等待该 Broker 专属的  RequestSendThread 线程处理
class ControllerChannelManager(controllerContext: ControllerContext,
                               config: KafkaConfig,
                               time: Time,
                               metrics: Metrics,
                               stateChangeLogger: StateChangeLogger,
                               threadNamePrefix: Option[String] = None) extends Logging with KafkaMetricsGroup {
  import ControllerChannelManager._

  // 核心属性，用一个 Map 存储 Broker 相关信息，具体形式是
  // <Broker ID, Broker 关键信息>
  // ControllerBrokerStateInfo 这个类就是一个 POJO 类
  // 从 POJO 类中存储的信息就可以看出来，Controller 为每个目标 Broker 都创建了一个消息队列
  // 因为 HashMap 并不是线程安全的，所以 ControllerChannelManager 下面的5个 public 方法都加了 brokerLock 锁，
  // 以保证 brokerStateInfo 的并发安全性
  protected val brokerStateInfo = new HashMap[Int, ControllerBrokerStateInfo]
  private val brokerLock = new Object
  this.logIdent = "[Channel manager on controller " + config.brokerId + "]: "

  newGauge("TotalQueueSize",
    () => brokerLock synchronized {
      brokerStateInfo.values.iterator.map(_.messageQueue.size).sum
    }
  )

  /**
   * Controller 组件在启动时会调用这个方法，
   * 该方法会从元数据信息中找到集群的 Broker 列表，依次调用 addNewBroker 方法，将这些 Broker 添加到 brokerStateInfo 属性中，
   * 最后再其次启动 brokerStateInfo 中的 RequestSendThread 线程
   */
  def startup() = {
    controllerContext.liveOrShuttingDownBrokers.foreach(addNewBroker)

    brokerLock synchronized {
      brokerStateInfo.foreach(brokerState => startRequestSendThread(brokerState._1))
    }
  }

  /**
   * 关闭所有 RequestSendThread 进程，并清空必要的资源
   */
  def shutdown() = {
    brokerLock synchronized {
      brokerStateInfo.values.toList.foreach(removeExistingBroker)
    }
  }

  /**
   * 发送请求，在这个方法中只是将请求对象提交到阻塞队列中
   * @param brokerId
   * @param request
   * @param callback
   */
  def sendRequest(brokerId: Int, request: AbstractControlRequest.Builder[_ <: AbstractControlRequest],
                  callback: AbstractResponse => Unit = null): Unit = {
    brokerLock synchronized {
      val stateInfoOpt = brokerStateInfo.get(brokerId)
      stateInfoOpt match {
        case Some(stateInfo) =>
          stateInfo.messageQueue.put(QueueItem(request.apiKey, request, callback, time.milliseconds()))
        case None =>
          warn(s"Not sending request $request to broker $brokerId, since it is offline.")
      }
    }
  }

  /**
   * 将指定 Broker 添加到 brokerStateInfo 中，并创建相应的必要资源，
   * 并启动 RequestSendThread 线程
   * @param broker
   */
  def addBroker(broker: Broker): Unit = {
    // be careful here. Maybe the startup() API has already started the request send thread
    brokerLock synchronized {
      // 如果是新的 Broker
      if (!brokerStateInfo.contains(broker.id)) {
        // 将这个新的 Broker 加入到 Controller 的管理中，并创建对应的 RequestSendThread 线程
        addNewBroker(broker)
        // 启动 RequestSendThread 线程
        startRequestSendThread(broker.id)
      }
    }
  }

  /**
   * 从 brokerStateInfo 中移除指定 Broker 相关数据
   * @param brokerId
   */
  def removeBroker(brokerId: Int): Unit = {
    brokerLock synchronized {
      removeExistingBroker(brokerStateInfo(brokerId))
    }
  }

  /**
   * 将指定 Broker 节点添加到 brokerStateInfo 中，并创建一系列对应资源
   * @param broker
   */
  private def addNewBroker(broker: Broker): Unit = {
    // 为指定 Broker 创建请求阻塞队列
    val messageQueue = new LinkedBlockingQueue[QueueItem]
    debug(s"Controller ${config.brokerId} trying to connect to broker ${broker.id}")
    val controllerToBrokerListenerName = config.controlPlaneListenerName.getOrElse(config.interBrokerListenerName)
    val controllerToBrokerSecurityProtocol = config.controlPlaneSecurityProtocol.getOrElse(config.interBrokerSecurityProtocol)
    // 获取待连接 Broker 节点信息
    val brokerNode = broker.node(controllerToBrokerListenerName)
    val logContext = new LogContext(s"[Controller id=${config.brokerId}, targetBrokerId=${brokerNode.idString}] ")
    val (networkClient, reconfigurableChannelBuilder) = {
      val channelBuilder = ChannelBuilders.clientChannelBuilder(
        controllerToBrokerSecurityProtocol,
        JaasContext.Type.SERVER,
        config,
        controllerToBrokerListenerName,
        config.saslMechanismInterBrokerProtocol,
        time,
        config.saslInterBrokerHandshakeRequestEnable,
        logContext
      )
      val reconfigurableChannelBuilder = channelBuilder match {
        case reconfigurable: Reconfigurable =>
          config.addReconfigurable(reconfigurable)
          Some(reconfigurable)
        case _ => None
      }
      // 创建 NIO Selector 实例，用于网络数据传输
      val selector = new Selector(
        NetworkReceive.UNLIMITED,
        Selector.NO_IDLE_TIMEOUT_MS,
        metrics,
        time,
        "controller-channel",
        Map("broker-id" -> brokerNode.idString).asJava,
        false,
        channelBuilder,
        logContext
      )
      // 创建 NetworkClient 实例，
      // 该类是 Kafka clients 封装的顶层网络客户端 API，其提供了丰富的方法支持网络层 IO 数据传输
      val networkClient = new NetworkClient(
        selector,
        new ManualMetadataUpdater(Seq(brokerNode).asJava),
        config.brokerId.toString,
        1,
        0,
        0,
        Selectable.USE_DEFAULT_BUFFER_SIZE,
        Selectable.USE_DEFAULT_BUFFER_SIZE,
        config.requestTimeoutMs,
        config.connectionSetupTimeoutMs,
        config.connectionSetupTimeoutMaxMs,
        time,
        false,
        new ApiVersions,
        logContext
      )
      (networkClient, reconfigurableChannelBuilder)
    }
    // 设定 RequestSendThread 线程名称
    val threadName = threadNamePrefix match {
      case None => s"Controller-${config.brokerId}-to-broker-${broker.id}-send-thread"
      case Some(name) => s"$name:Controller-${config.brokerId}-to-broker-${broker.id}-send-thread"
    }

    // 构造请求处理速率监控指标
    val requestRateAndQueueTimeMetrics = newTimer(
      RequestRateAndQueueTimeMetricName, TimeUnit.MILLISECONDS, TimeUnit.SECONDS, brokerMetricTags(broker.id)
    )

    // 创建 RequestSendThread 实例
    val requestThread = new RequestSendThread(config.brokerId, controllerContext, messageQueue, networkClient,
      brokerNode, config, time, requestRateAndQueueTimeMetrics, stateChangeLogger, threadName)
    requestThread.setDaemon(false)

    val queueSizeGauge = newGauge(QueueSizeMetricName, () => messageQueue.size, brokerMetricTags(broker.id))

    // 创建当前 Broker 对应的 ControllerBrokerStateInfo 实例，并将其加入到 brokerStateInfo 进行统一管理
    brokerStateInfo.put(broker.id, ControllerBrokerStateInfo(networkClient, brokerNode, messageQueue,
      requestThread, queueSizeGauge, requestRateAndQueueTimeMetrics, reconfigurableChannelBuilder))
  }

  private def brokerMetricTags(brokerId: Int) = Map("broker-id" -> brokerId.toString)

  private def removeExistingBroker(brokerState: ControllerBrokerStateInfo): Unit = {
    try {
      // Shutdown the RequestSendThread before closing the NetworkClient to avoid the concurrent use of the
      // non-threadsafe classes as described in KAFKA-4959.
      // The call to shutdownLatch.await() in ShutdownableThread.shutdown() serves as a synchronization barrier that
      // hands off the NetworkClient from the RequestSendThread to the ZkEventThread.
      brokerState.reconfigurableChannelBuilder.foreach(config.removeReconfigurable)
      brokerState.requestSendThread.shutdown()
      brokerState.networkClient.close()
      brokerState.messageQueue.clear()
      removeMetric(QueueSizeMetricName, brokerMetricTags(brokerState.brokerNode.id))
      removeMetric(RequestRateAndQueueTimeMetricName, brokerMetricTags(brokerState.brokerNode.id))
      brokerStateInfo.remove(brokerState.brokerNode.id)
    } catch {
      case e: Throwable => error("Error while removing broker by the controller", e)
    }
  }

  /**
   * 启动指定 Broker 对应的 RequestSendThread 线程
   * @param brokerId
   */
  protected def startRequestSendThread(brokerId: Int): Unit = {
    // 获取指定 Broker 所对应的 RequestSendThread 线程实例
    val requestThread = brokerStateInfo(brokerId).requestSendThread
    if (requestThread.getState == Thread.State.NEW) {
      // 启动线程
      requestThread.start()
    }
  }
}

// Controller 事件处理线程(生产者)发送的请求在阻塞队列中的存储形式(阻塞队列中的元素)
case class QueueItem(apiKey: ApiKeys, request: AbstractControlRequest.Builder[_ <: AbstractControlRequest],
                     callback: AbstractResponse => Unit, enqueueTimeMs: Long)

// RequestSendThread 负责从阻塞队列中取出请求，并将其发往具体的 Broker 中，相当于消费者
class RequestSendThread(val controllerId: Int, // Controller 所在的 Broker ID
                        val controllerContext: ControllerContext, // Controller 元数据信息
                        val queue: BlockingQueue[QueueItem], // 请求的阻塞队列
                        val networkClient: NetworkClient, // 用于执行请求发送的网络 I/O 类
                        val brokerNode: Node, // 目标 Broker 节点
                        val config: KafkaConfig, // Kafka 配置信息
                        val time: Time,
                        val requestRateAndQueueTimeMetrics: Timer,
                        val stateChangeLogger: StateChangeLogger,
                        name: String)
  extends ShutdownableThread(name = name) {

  logIdent = s"[RequestSendThread controllerId=$controllerId] "

  private val socketTimeoutMs = config.controllerSocketTimeoutMs

  // RequestSendThread 类的核心方法，执行线程的具体逻辑
  override def doWork(): Unit = {

    def backoff(): Unit = pause(100, TimeUnit.MILLISECONDS)

    val QueueItem(apiKey, requestBuilder, callback, enqueueTimeMs) = queue.take()
    requestRateAndQueueTimeMetrics.update(time.milliseconds() - enqueueTimeMs, TimeUnit.MILLISECONDS)

    var clientResponse: ClientResponse = null
    try {
      var isSendSuccessful = false
      while (isRunning && !isSendSuccessful) {
        // if a broker goes down for a long time, then at some point the controller's zookeeper listener will trigger a
        // removeBroker which will invoke shutdown() on this thread. At that point, we will stop retrying.
        try {
          // 如果没有创建与目标 Broker 的 TCP 连接，或连接暂时不可用
          if (!brokerReady()) {
            isSendSuccessful = false
            // 等待重试
            backoff()
          }
          else {
            val clientRequest = networkClient.newClientRequest(brokerNode.idString, requestBuilder,
              time.milliseconds(), true)
            // 发送请求，等待接收 response
            clientResponse = NetworkClientUtils.sendAndReceive(networkClient, clientRequest, time)
            isSendSuccessful = true
          }
        } catch {
          case e: Throwable => // if the send was not successful, reconnect to broker and resend the message
            warn(s"Controller $controllerId epoch ${controllerContext.epoch} fails to send request $requestBuilder " +
              s"to broker $brokerNode. Reconnecting to broker.", e)
            // 如果出现异常，需要关闭与对应 Broker 的连接
            networkClient.close(brokerNode.idString)
            isSendSuccessful = false
            backoff()
        }
      }
      // 如果接收到了 Response
      if (clientResponse != null) {
        val requestHeader = clientResponse.requestHeader
        val api = requestHeader.apiKey
        // Response 的类型必须是 LeaderAndIsrRequest、StopReplicaRequest、UpdateMetadataRequest 中的一种
        if (api != ApiKeys.LEADER_AND_ISR && api != ApiKeys.STOP_REPLICA && api != ApiKeys.UPDATE_METADATA)
          throw new KafkaException(s"Unexpected apiKey received: $apiKey")

        val response = clientResponse.responseBody

        stateChangeLogger.withControllerEpoch(controllerContext.epoch).trace(s"Received response " +
          s"$response for request $api with correlation id " +
          s"${requestHeader.correlationId} sent to broker $brokerNode")

        // 处理回调
        if (callback != null) {
          callback(response)
        }
      }
    } catch {
      case e: Throwable =>
        error(s"Controller $controllerId fails to send a request to broker $brokerNode", e)
        // If there is any socket error (eg, socket timeout), the connection is no longer usable and needs to be recreated.
        networkClient.close(brokerNode.idString)
    }
  }

  private def brokerReady(): Boolean = {
    try {
      if (!NetworkClientUtils.isReady(networkClient, brokerNode, time.milliseconds())) {
        if (!NetworkClientUtils.awaitReady(networkClient, brokerNode, time, socketTimeoutMs))
          throw new SocketTimeoutException(s"Failed to connect within $socketTimeoutMs ms")

        info(s"Controller $controllerId connected to $brokerNode for sending state change requests")
      }

      true
    } catch {
      case e: Throwable =>
        warn(s"Controller $controllerId's connection to broker $brokerNode was unsuccessful", e)
        networkClient.close(brokerNode.idString)
        false
    }
  }

  override def initiateShutdown(): Boolean = {
    if (super.initiateShutdown()) {
      networkClient.initiateClose()
      true
    } else
      false
  }
}

class ControllerBrokerRequestBatch(config: KafkaConfig,
                                   controllerChannelManager: ControllerChannelManager,
                                   controllerEventManager: ControllerEventManager,
                                   controllerContext: ControllerContext,
                                   stateChangeLogger: StateChangeLogger)
  extends AbstractControllerBrokerRequestBatch(config, controllerContext, stateChangeLogger) {

  def sendEvent(event: ControllerEvent): Unit = {
    controllerEventManager.put(event)
  }

  def sendRequest(brokerId: Int,
                  request: AbstractControlRequest.Builder[_ <: AbstractControlRequest],
                  callback: AbstractResponse => Unit = null): Unit = {
    controllerChannelManager.sendRequest(brokerId, request, callback)
  }

}

abstract class AbstractControllerBrokerRequestBatch(config: KafkaConfig,
                                                    controllerContext: ControllerContext,
                                                    stateChangeLogger: StateChangeLogger) extends Logging {
  val controllerId: Int = config.brokerId
  val leaderAndIsrRequestMap = mutable.Map.empty[Int, mutable.Map[TopicPartition, LeaderAndIsrPartitionState]]
  val stopReplicaRequestMap = mutable.Map.empty[Int, mutable.Map[TopicPartition, StopReplicaPartitionState]]
  val updateMetadataRequestBrokerSet = mutable.Set.empty[Int]
  val updateMetadataRequestPartitionInfoMap = mutable.Map.empty[TopicPartition, UpdateMetadataPartitionState]

  def sendEvent(event: ControllerEvent): Unit

  def sendRequest(brokerId: Int,
                  request: AbstractControlRequest.Builder[_ <: AbstractControlRequest],
                  callback: AbstractResponse => Unit = null): Unit

  /**
   * 清空 Controller 待发送请求集合，准备发送本次请求，
   * 这里的清空是指如果请求集合不为空，就会直接报错
   */
  def newBatch(): Unit = {
    // raise error if the previous batch is not empty
    if (leaderAndIsrRequestMap.nonEmpty)
      throw new IllegalStateException("Controller to broker state change requests batch is not empty while creating " +
        s"a new one. Some LeaderAndIsr state changes $leaderAndIsrRequestMap might be lost ")
    if (stopReplicaRequestMap.nonEmpty)
      throw new IllegalStateException("Controller to broker state change requests batch is not empty while creating a " +
        s"new one. Some StopReplica state changes $stopReplicaRequestMap might be lost ")
    if (updateMetadataRequestBrokerSet.nonEmpty)
      throw new IllegalStateException("Controller to broker state change requests batch is not empty while creating a " +
        s"new one. Some UpdateMetadata state changes to brokers $updateMetadataRequestBrokerSet with partition info " +
        s"$updateMetadataRequestPartitionInfoMap might be lost ")
  }

  def clear(): Unit = {
    leaderAndIsrRequestMap.clear()
    stopReplicaRequestMap.clear()
    updateMetadataRequestBrokerSet.clear()
    updateMetadataRequestPartitionInfoMap.clear()
  }

  def addLeaderAndIsrRequestForBrokers(brokerIds: Seq[Int],
                                       topicPartition: TopicPartition,
                                       leaderIsrAndControllerEpoch: LeaderIsrAndControllerEpoch,
                                       replicaAssignment: ReplicaAssignment,
                                       isNew: Boolean): Unit = {

    brokerIds.filter(_ >= 0).foreach { brokerId =>
      val result = leaderAndIsrRequestMap.getOrElseUpdate(brokerId, mutable.Map.empty)
      val alreadyNew = result.get(topicPartition).exists(_.isNew)
      val leaderAndIsr = leaderIsrAndControllerEpoch.leaderAndIsr
      result.put(topicPartition, new LeaderAndIsrPartitionState()
        .setTopicName(topicPartition.topic)
        .setPartitionIndex(topicPartition.partition)
        .setControllerEpoch(leaderIsrAndControllerEpoch.controllerEpoch)
        .setLeader(leaderAndIsr.leader)
        .setLeaderEpoch(leaderAndIsr.leaderEpoch)
        .setIsr(leaderAndIsr.isr.map(Integer.valueOf).asJava)
        .setZkVersion(leaderAndIsr.zkVersion)
        .setReplicas(replicaAssignment.replicas.map(Integer.valueOf).asJava)
        .setAddingReplicas(replicaAssignment.addingReplicas.map(Integer.valueOf).asJava)
        .setRemovingReplicas(replicaAssignment.removingReplicas.map(Integer.valueOf).asJava)
        .setIsNew(isNew || alreadyNew))
    }

    addUpdateMetadataRequestForBrokers(controllerContext.liveOrShuttingDownBrokerIds.toSeq, Set(topicPartition))
  }

  def addStopReplicaRequestForBrokers(brokerIds: Seq[Int],
                                      topicPartition: TopicPartition,
                                      deletePartition: Boolean): Unit = {
    // A sentinel (-2) is used as an epoch if the topic is queued for deletion. It overrides
    // any existing epoch.
    val leaderEpoch = if (controllerContext.isTopicQueuedUpForDeletion(topicPartition.topic)) {
      LeaderAndIsr.EpochDuringDelete
    } else {
      controllerContext.partitionLeadershipInfo(topicPartition)
        .map(_.leaderAndIsr.leaderEpoch)
        .getOrElse(LeaderAndIsr.NoEpoch)
    }

    brokerIds.filter(_ >= 0).foreach { brokerId =>
      val result = stopReplicaRequestMap.getOrElseUpdate(brokerId, mutable.Map.empty)
      val alreadyDelete = result.get(topicPartition).exists(_.deletePartition)
      result.put(topicPartition, new StopReplicaPartitionState()
          .setPartitionIndex(topicPartition.partition())
          .setLeaderEpoch(leaderEpoch)
          .setDeletePartition(alreadyDelete || deletePartition))
    }
  }

  /** Send UpdateMetadataRequest to the given brokers for the given partitions and partitions that are being deleted */
  def addUpdateMetadataRequestForBrokers(brokerIds: Seq[Int],
                                         partitions: collection.Set[TopicPartition]): Unit = {

    def updateMetadataRequestPartitionInfo(partition: TopicPartition, beingDeleted: Boolean): Unit = {
      controllerContext.partitionLeadershipInfo(partition) match {
        case Some(LeaderIsrAndControllerEpoch(leaderAndIsr, controllerEpoch)) =>
          val replicas = controllerContext.partitionReplicaAssignment(partition)
          val offlineReplicas = replicas.filter(!controllerContext.isReplicaOnline(_, partition))
          val updatedLeaderAndIsr =
            if (beingDeleted) LeaderAndIsr.duringDelete(leaderAndIsr.isr)
            else leaderAndIsr

          val partitionStateInfo = new UpdateMetadataPartitionState()
            .setTopicName(partition.topic)
            .setPartitionIndex(partition.partition)
            .setControllerEpoch(controllerEpoch)
            .setLeader(updatedLeaderAndIsr.leader)
            .setLeaderEpoch(updatedLeaderAndIsr.leaderEpoch)
            .setIsr(updatedLeaderAndIsr.isr.map(Integer.valueOf).asJava)
            .setZkVersion(updatedLeaderAndIsr.zkVersion)
            .setReplicas(replicas.map(Integer.valueOf).asJava)
            .setOfflineReplicas(offlineReplicas.map(Integer.valueOf).asJava)
          updateMetadataRequestPartitionInfoMap.put(partition, partitionStateInfo)

        case None =>
          info(s"Leader not yet assigned for partition $partition. Skip sending UpdateMetadataRequest.")
      }
    }

    updateMetadataRequestBrokerSet ++= brokerIds.filter(_ >= 0)
    partitions.foreach(partition => updateMetadataRequestPartitionInfo(partition,
      beingDeleted = controllerContext.topicsToBeDeleted.contains(partition.topic)))
  }

  private def sendLeaderAndIsrRequest(controllerEpoch: Int, stateChangeLog: StateChangeLogger): Unit = {
    val leaderAndIsrRequestVersion: Short =
      if (config.interBrokerProtocolVersion >= KAFKA_2_8_IV1) 5
      else if (config.interBrokerProtocolVersion >= KAFKA_2_4_IV1) 4
      else if (config.interBrokerProtocolVersion >= KAFKA_2_4_IV0) 3
      else if (config.interBrokerProtocolVersion >= KAFKA_2_2_IV0) 2
      else if (config.interBrokerProtocolVersion >= KAFKA_1_0_IV0) 1
      else 0

    leaderAndIsrRequestMap.forKeyValue { (broker, leaderAndIsrPartitionStates) =>
      if (controllerContext.liveOrShuttingDownBrokerIds.contains(broker)) {
        val leaderIds = mutable.Set.empty[Int]
        var numBecomeLeaders = 0
        leaderAndIsrPartitionStates.forKeyValue { (topicPartition, state) =>
          leaderIds += state.leader
          val typeOfRequest = if (broker == state.leader) {
            numBecomeLeaders += 1
            "become-leader"
          } else {
            "become-follower"
          }
          if (stateChangeLog.isTraceEnabled)
            stateChangeLog.trace(s"Sending $typeOfRequest LeaderAndIsr request $state to broker $broker for partition $topicPartition")
        }
        stateChangeLog.info(s"Sending LeaderAndIsr request to broker $broker with $numBecomeLeaders become-leader " +
          s"and ${leaderAndIsrPartitionStates.size - numBecomeLeaders} become-follower partitions")
        val leaders = controllerContext.liveOrShuttingDownBrokers.filter(b => leaderIds.contains(b.id)).map {
          _.node(config.interBrokerListenerName)
        }
        val brokerEpoch = controllerContext.liveBrokerIdAndEpochs(broker)
        val topicIds = leaderAndIsrPartitionStates.keys
          .map(_.topic)
          .toSet[String]
          .map(topic => (topic, controllerContext.topicIds.getOrElse(topic, Uuid.ZERO_UUID)))
          .toMap
        val leaderAndIsrRequestBuilder = new LeaderAndIsrRequest.Builder(leaderAndIsrRequestVersion, controllerId,
          controllerEpoch, brokerEpoch, leaderAndIsrPartitionStates.values.toBuffer.asJava, topicIds.asJava, leaders.asJava)
        sendRequest(broker, leaderAndIsrRequestBuilder, (r: AbstractResponse) => {
          val leaderAndIsrResponse = r.asInstanceOf[LeaderAndIsrResponse]
          sendEvent(LeaderAndIsrResponseReceived(leaderAndIsrResponse, broker))
        })
      }
    }
    leaderAndIsrRequestMap.clear()
  }

  private def sendUpdateMetadataRequests(controllerEpoch: Int, stateChangeLog: StateChangeLogger): Unit = {
    stateChangeLog.info(s"Sending UpdateMetadata request to brokers $updateMetadataRequestBrokerSet " +
      s"for ${updateMetadataRequestPartitionInfoMap.size} partitions")

    val partitionStates = updateMetadataRequestPartitionInfoMap.values.toBuffer
    val updateMetadataRequestVersion: Short =
      if (config.interBrokerProtocolVersion >= KAFKA_2_8_IV1) 7
      else if (config.interBrokerProtocolVersion >= KAFKA_2_4_IV1) 6
      else if (config.interBrokerProtocolVersion >= KAFKA_2_2_IV0) 5
      else if (config.interBrokerProtocolVersion >= KAFKA_1_0_IV0) 4
      else if (config.interBrokerProtocolVersion >= KAFKA_0_10_2_IV0) 3
      else if (config.interBrokerProtocolVersion >= KAFKA_0_10_0_IV1) 2
      else if (config.interBrokerProtocolVersion >= KAFKA_0_9_0) 1
      else 0

    val liveBrokers = controllerContext.liveOrShuttingDownBrokers.iterator.map { broker =>
      val endpoints = if (updateMetadataRequestVersion == 0) {
        // Version 0 of UpdateMetadataRequest only supports PLAINTEXT
        val securityProtocol = SecurityProtocol.PLAINTEXT
        val listenerName = ListenerName.forSecurityProtocol(securityProtocol)
        val node = broker.node(listenerName)
        Seq(new UpdateMetadataEndpoint()
          .setHost(node.host)
          .setPort(node.port)
          .setSecurityProtocol(securityProtocol.id)
          .setListener(listenerName.value))
      } else {
        broker.endPoints.map { endpoint =>
          new UpdateMetadataEndpoint()
            .setHost(endpoint.host)
            .setPort(endpoint.port)
            .setSecurityProtocol(endpoint.securityProtocol.id)
            .setListener(endpoint.listenerName.value)
        }
      }
      new UpdateMetadataBroker()
        .setId(broker.id)
        .setEndpoints(endpoints.asJava)
        .setRack(broker.rack.orNull)
    }.toBuffer

    updateMetadataRequestBrokerSet.intersect(controllerContext.liveOrShuttingDownBrokerIds).foreach { broker =>
      val brokerEpoch = controllerContext.liveBrokerIdAndEpochs(broker)
      val topicIds = partitionStates.map(_.topicName())
        .distinct
        .filter(controllerContext.topicIds.contains)
        .map(topic => (topic, controllerContext.topicIds(topic))).toMap
      val updateMetadataRequestBuilder = new UpdateMetadataRequest.Builder(updateMetadataRequestVersion,
        controllerId, controllerEpoch, brokerEpoch, partitionStates.asJava, liveBrokers.asJava, topicIds.asJava)
      sendRequest(broker, updateMetadataRequestBuilder, (r: AbstractResponse) => {
        val updateMetadataResponse = r.asInstanceOf[UpdateMetadataResponse]
        sendEvent(UpdateMetadataResponseReceived(updateMetadataResponse, broker))
      })

    }
    updateMetadataRequestBrokerSet.clear()
    updateMetadataRequestPartitionInfoMap.clear()
  }

  private def sendStopReplicaRequests(controllerEpoch: Int, stateChangeLog: StateChangeLogger): Unit = {
    val traceEnabled = stateChangeLog.isTraceEnabled
    val stopReplicaRequestVersion: Short =
      if (config.interBrokerProtocolVersion >= KAFKA_2_6_IV0) 3
      else if (config.interBrokerProtocolVersion >= KAFKA_2_4_IV1) 2
      else if (config.interBrokerProtocolVersion >= KAFKA_2_2_IV0) 1
      else 0

    def responseCallback(brokerId: Int, isPartitionDeleted: TopicPartition => Boolean)
                        (response: AbstractResponse): Unit = {
      val stopReplicaResponse = response.asInstanceOf[StopReplicaResponse]
      val partitionErrorsForDeletingTopics = mutable.Map.empty[TopicPartition, Errors]
      stopReplicaResponse.partitionErrors.forEach { pe =>
        val tp = new TopicPartition(pe.topicName, pe.partitionIndex)
        if (controllerContext.isTopicDeletionInProgress(pe.topicName) &&
            isPartitionDeleted(tp)) {
          partitionErrorsForDeletingTopics += tp -> Errors.forCode(pe.errorCode)
        }
      }
      if (partitionErrorsForDeletingTopics.nonEmpty)
        sendEvent(TopicDeletionStopReplicaResponseReceived(brokerId, stopReplicaResponse.error,
          partitionErrorsForDeletingTopics))
    }

    stopReplicaRequestMap.forKeyValue { (brokerId, partitionStates) =>
      if (controllerContext.liveOrShuttingDownBrokerIds.contains(brokerId)) {
        if (traceEnabled)
          partitionStates.forKeyValue { (topicPartition, partitionState) =>
            stateChangeLog.trace(s"Sending StopReplica request $partitionState to " +
              s"broker $brokerId for partition $topicPartition")
          }

        val brokerEpoch = controllerContext.liveBrokerIdAndEpochs(brokerId)
        if (stopReplicaRequestVersion >= 3) {
          val stopReplicaTopicState = mutable.Map.empty[String, StopReplicaTopicState]
          partitionStates.forKeyValue { (topicPartition, partitionState) =>
            val topicState = stopReplicaTopicState.getOrElseUpdate(topicPartition.topic,
              new StopReplicaTopicState().setTopicName(topicPartition.topic))
            topicState.partitionStates().add(partitionState)
          }

          stateChangeLog.info(s"Sending StopReplica request for ${partitionStates.size} " +
            s"replicas to broker $brokerId")
          val stopReplicaRequestBuilder = new StopReplicaRequest.Builder(
            stopReplicaRequestVersion, controllerId, controllerEpoch, brokerEpoch,
            false, stopReplicaTopicState.values.toBuffer.asJava)
          sendRequest(brokerId, stopReplicaRequestBuilder,
            responseCallback(brokerId, tp => partitionStates.get(tp).exists(_.deletePartition)))
        } else {
          var numPartitionStateWithDelete = 0
          var numPartitionStateWithoutDelete = 0
          val topicStatesWithDelete = mutable.Map.empty[String, StopReplicaTopicState]
          val topicStatesWithoutDelete = mutable.Map.empty[String, StopReplicaTopicState]

          partitionStates.forKeyValue { (topicPartition, partitionState) =>
            val topicStates = if (partitionState.deletePartition()) {
              numPartitionStateWithDelete += 1
              topicStatesWithDelete
            } else {
              numPartitionStateWithoutDelete += 1
              topicStatesWithoutDelete
            }
            val topicState = topicStates.getOrElseUpdate(topicPartition.topic,
              new StopReplicaTopicState().setTopicName(topicPartition.topic))
            topicState.partitionStates().add(partitionState)
          }

          if (topicStatesWithDelete.nonEmpty) {
            stateChangeLog.info(s"Sending StopReplica request (delete = true) for " +
              s"$numPartitionStateWithDelete replicas to broker $brokerId")
            val stopReplicaRequestBuilder = new StopReplicaRequest.Builder(
              stopReplicaRequestVersion, controllerId, controllerEpoch, brokerEpoch,
              true, topicStatesWithDelete.values.toBuffer.asJava)
            sendRequest(brokerId, stopReplicaRequestBuilder, responseCallback(brokerId, _ => true))
          }

          if (topicStatesWithoutDelete.nonEmpty) {
            stateChangeLog.info(s"Sending StopReplica request (delete = false) for " +
              s"$numPartitionStateWithoutDelete replicas to broker $brokerId")
            val stopReplicaRequestBuilder = new StopReplicaRequest.Builder(
              stopReplicaRequestVersion, controllerId, controllerEpoch, brokerEpoch,
              false, topicStatesWithoutDelete.values.toBuffer.asJava)
            sendRequest(brokerId, stopReplicaRequestBuilder)
          }
        }
      }
    }

    stopReplicaRequestMap.clear()
  }

  def sendRequestsToBrokers(controllerEpoch: Int): Unit = {
    try {
      val stateChangeLog = stateChangeLogger.withControllerEpoch(controllerEpoch)
      sendLeaderAndIsrRequest(controllerEpoch, stateChangeLog)
      sendUpdateMetadataRequests(controllerEpoch, stateChangeLog)
      sendStopReplicaRequests(controllerEpoch, stateChangeLog)
    } catch {
      case e: Throwable =>
        if (leaderAndIsrRequestMap.nonEmpty) {
          error("Haven't been able to send leader and isr requests, current state of " +
            s"the map is $leaderAndIsrRequestMap. Exception message: $e")
        }
        if (updateMetadataRequestBrokerSet.nonEmpty) {
          error(s"Haven't been able to send metadata update requests to brokers $updateMetadataRequestBrokerSet, " +
            s"current state of the partition info is $updateMetadataRequestPartitionInfoMap. Exception message: $e")
        }
        if (stopReplicaRequestMap.nonEmpty) {
          error("Haven't been able to send stop replica requests, current state of " +
            s"the map is $stopReplicaRequestMap. Exception message: $e")
        }
        throw new IllegalStateException(e)
    }
  }
}

// POJO 类，存储 Broker 关键信息
case class ControllerBrokerStateInfo(networkClient: NetworkClient,
                                     brokerNode: Node, // 目标 Broker 节点对象
                                     messageQueue: BlockingQueue[QueueItem], // 请求消息阻塞队列
                                     requestSendThread: RequestSendThread, // 发送请求的线程
                                     queueSizeGauge: Gauge[Int],
                                     requestRateAndTimeMetrics: Timer,
                                     reconfigurableChannelBuilder: Option[Reconfigurable])

