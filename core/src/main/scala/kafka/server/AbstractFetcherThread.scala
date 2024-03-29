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

package kafka.server

import kafka.cluster.BrokerEndPoint
import kafka.common.ClientIdAndBroker
import kafka.log.LogAppendInfo
import kafka.metrics.KafkaMetricsGroup
import kafka.server.AbstractFetcherThread.{ReplicaFetch, ResultWithPartitions}
import kafka.utils.CoreUtils.inLock
import kafka.utils.Implicits._
import kafka.utils.{DelayedItem, Pool, ShutdownableThread}
import org.apache.kafka.common.errors._
import org.apache.kafka.common.internals.PartitionStates
import org.apache.kafka.common.message.OffsetForLeaderEpochResponseData.EpochEndOffset
import org.apache.kafka.common.message.{FetchResponseData, OffsetForLeaderEpochRequestData}
import org.apache.kafka.common.protocol.Errors
import org.apache.kafka.common.record.{FileRecords, MemoryRecords, Records}
import org.apache.kafka.common.requests.OffsetsForLeaderEpochResponse.{UNDEFINED_EPOCH, UNDEFINED_EPOCH_OFFSET}
import org.apache.kafka.common.requests._
import org.apache.kafka.common.{InvalidRecordException, TopicPartition}

import java.nio.ByteBuffer
import java.util
import java.util.Optional
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import scala.collection.{Map, Set, mutable}
import scala.compat.java8.OptionConverters._
import scala.jdk.CollectionConverters._
import scala.math._

/**
 * Abstract class for fetching data from multiple partitions from the same broker.
 * 
 * 
 * @param name 线程名称
 * @param clientId 客户端ID（对于 Follower 副本而言，该值就是线程的名称）
 * @param sourceBroker 源 Broker 节点信息，定义从哪个 Broker 上读取数据
 * @param failedPartitions 线程处理过程中报错的分区集合，用于计算特定的统计指标
 * @param fetchBackOffMs 获取分区数据出错后的等待重试间隔，默认是 replica.fetch.backoff.ms 参数值
 * @param isInterruptible 线程关闭时是否允许中断该线程（对于 Follower 副本而言，它创建的 ReplicaFetcherThread 线程是不可中断的）
 * @param brokerTopicStats Broker 端主题的各类监控指标，常见的有 MessagesInPerSec、BytesInPerSec 等
 */
abstract class AbstractFetcherThread(name: String,
                                     clientId: String,
                                     val sourceBroker: BrokerEndPoint,
                                     failedPartitions: FailedPartitions,
                                     fetchBackOffMs: Int = 0,
                                     isInterruptible: Boolean = true,
                                     val brokerTopicStats: BrokerTopicStats) //BrokerTopicStats's lifecycle managed by ReplicaManager
  extends ShutdownableThread(name, isInterruptible) {

  /**
   * 保存 Response 中单个分区拉取的各项数据
   */
  type FetchData = FetchResponseData.PartitionData
  /**
   * 保存 Leader Epoch 数据
   */
  type EpochData = OffsetForLeaderEpochRequestData.OffsetForLeaderPartition
  /**
   * 分区状态
   */
  private val partitionStates = new PartitionStates[PartitionFetchState]
  protected val partitionMapLock = new ReentrantLock
  private val partitionMapCond = partitionMapLock.newCondition()

  private val metricId = ClientIdAndBroker(clientId, sourceBroker.host, sourceBroker.port)
  val fetcherStats = new FetcherStats(metricId)
  val fetcherLagStats = new FetcherLagStats(metricId)

  /* callbacks to be defined in subclass */


  /**
   * process fetched data
   * 处理读取回来的消息集合
   *
   * @param topicPartition
   * @param fetchOffset
   * @param partitionData
   * @return Option[LogAppendInfo] 写入已读取消息数据前的元数据，对于 Follower 副本读消息写入日志而言，一定不为空
   */
  protected def processPartitionData(topicPartition: TopicPartition,
                                     fetchOffset: Long,
                                     partitionData: FetchData): Option[LogAppendInfo]

  /**
   * 负责执行日志截断逻辑
   * @param topicPartition 主题分区
   * @param truncationState offset + 截断状态
   */
  protected def truncate(topicPartition: TopicPartition, truncationState: OffsetTruncationState): Unit

  protected def truncateFullyAndStartAt(topicPartition: TopicPartition, offset: Long): Unit

  /**
   * 负责构建拉取消息的请求，本质上就是为指定分区构建对应的 FetchRequest.Builder 对象
   * @param partitionMap 一组要读取的分区列表，分区是否可读取取决于 PartitionFetchState 中的状态
   * @return
   */
  protected def buildFetch(partitionMap: Map[TopicPartition, PartitionFetchState]): ResultWithPartitions[Option[ReplicaFetch]]

  protected def latestEpoch(topicPartition: TopicPartition): Option[Int]

  protected def logStartOffset(topicPartition: TopicPartition): Long

  protected def logEndOffset(topicPartition: TopicPartition): Long

  protected def endOffsetForEpoch(topicPartition: TopicPartition, epoch: Int): Option[OffsetAndEpoch]

  protected def fetchEpochEndOffsets(partitions: Map[TopicPartition, EpochData]): Map[TopicPartition, EpochEndOffset]

  protected def fetchFromLeader(fetchRequest: FetchRequest.Builder): Map[TopicPartition, FetchData]

  protected def fetchEarliestOffsetFromLeader(topicPartition: TopicPartition, currentLeaderEpoch: Int): Long

  protected def fetchLatestOffsetFromLeader(topicPartition: TopicPartition, currentLeaderEpoch: Int): Long

  protected val isOffsetForLeaderEpochSupported: Boolean

  protected val isTruncationOnFetchSupported: Boolean

  override def shutdown(): Unit = {
    initiateShutdown()
    inLock(partitionMapLock) {
      partitionMapCond.signalAll()
    }
    awaitShutdown()

    // we don't need the lock since the thread has finished shutdown and metric removal is safe
    fetcherStats.unregister()
    fetcherLagStats.unregister()
  }

  /**
   * 核心方法，运行线程的主逻辑，
   * AbstractFetcherThread 线程只要一直处于运行状态，就是会不断地重复执行截断操作和获取数据操作
   */
  override def doWork(): Unit = {
    // 执行副本截断操作，之所以需要执行截断操作，是因为分区的 Leader 可能会随时发生变化。
    // 每当有新 Leader 产生时，Follower 副本就必须主动执行截断操作，将自己的本地日志裁成与 Leader 一模一样的消息序列，
    // 而且 Leader 副本本身也需要执行截断操作，以此将 LEO 调整到分区高水位处。
    maybeTruncate()
    // 执行消息拉取操作
    maybeFetch()
  }

  private def maybeFetch(): Unit = {
    val fetchRequestOpt = inLock(partitionMapLock) {
      // 为 partitionStates 中的分区构造 FetchRequest，partitionStates 中保存的是要去获取消息的分区以及对应的状态
      val ResultWithPartitions(fetchRequestOpt, partitionsWithError) = buildFetch(partitionStates.partitionStateMap.asScala)

      // 处理出错的分区，处理方式主要是将这个分区加入到有序 Map 末尾，等待后续重试
      handlePartitionsWithErrors(partitionsWithError, "maybeFetch")

      // 如果当前没有可读取的分区，则等待 fetchBackOffMs 时间等候后续重试
      if (fetchRequestOpt.isEmpty) {
        trace(s"There are no active partitions. Back off for $fetchBackOffMs ms before sending a fetch request")
        partitionMapCond.await(fetchBackOffMs, TimeUnit.MILLISECONDS)
      }

      fetchRequestOpt
    }

    // 发送 FETCH 请求给 Leader 副本，并处理 Response
    fetchRequestOpt.foreach { case ReplicaFetch(sessionPartitions, fetchRequest) =>
      processFetchRequest(sessionPartitions, fetchRequest)
    }
  }

  /**
   * deal with partitions with errors, potentially due to leadership changes
   *
   * @param partitions 出错的分区
   * @param methodName 出错的对应方法名称
   */
  private def handlePartitionsWithErrors(partitions: Iterable[TopicPartition], methodName: String): Unit = {
    if (partitions.nonEmpty) {
      debug(s"Handling errors in $methodName for partitions $partitions")
      // 执行分区延迟重试
      delayPartitions(partitions, fetchBackOffMs)
    }
  }

  /**
   * Builds offset for leader epoch requests for partitions that are in the truncating phase based
   * on latest epochs of the future replicas (the one that is fetching)
   */
  private def fetchTruncatingPartitions(): (Map[TopicPartition, EpochData], Set[TopicPartition]) = inLock(partitionMapLock) {
    val partitionsWithEpochs = mutable.Map.empty[TopicPartition, EpochData]
    val partitionsWithoutEpochs = mutable.Set.empty[TopicPartition]

    partitionStates.partitionStateMap.forEach { (tp, state) =>
      if (state.isTruncating) {
        latestEpoch(tp) match {
          case Some(epoch) if isOffsetForLeaderEpochSupported =>
            partitionsWithEpochs += tp -> new EpochData()
              .setPartition(tp.partition)
              .setCurrentLeaderEpoch(state.currentLeaderEpoch)
              .setLeaderEpoch(epoch)
          case _ =>
            partitionsWithoutEpochs += tp
        }
      }
    }

    (partitionsWithEpochs, partitionsWithoutEpochs)
  }

  /**
   * 执行副本截断操作
   */
  private def maybeTruncate(): Unit = {
    // 将所有处于截断中状态的分区根据有无 Leader Epoch 进行分组
    val (partitionsWithEpochs, partitionsWithoutEpochs) = fetchTruncatingPartitions()
    // 有 Leader Epoch 的分区，将日志截断到 Leader Epoch 对应位移处
    if (partitionsWithEpochs.nonEmpty) {
      truncateToEpochEndOffsets(partitionsWithEpochs)
    }
    // 没有 Leader Epoch 的分区，将日志截断到高水位值处
    if (partitionsWithoutEpochs.nonEmpty) {
      truncateToHighWatermark(partitionsWithoutEpochs)
    }
  }

  private def doTruncate(topicPartition: TopicPartition, truncationState: OffsetTruncationState): Boolean = {
    try {
      truncate(topicPartition, truncationState)
      true
    }
    catch {
      case e: KafkaStorageException =>
        error(s"Failed to truncate $topicPartition at offset ${truncationState.offset}", e)
        markPartitionFailed(topicPartition)
        false
      case t: Throwable =>
        error(s"Unexpected error occurred during truncation for $topicPartition "
          + s"at offset ${truncationState.offset}", t)
        markPartitionFailed(topicPartition)
        false
    }
  }

  /**
    * - Build a leader epoch fetch based on partitions that are in the Truncating phase
    * - Send OffsetsForLeaderEpochRequest, retrieving the latest offset for each partition's
    *   leader epoch. This is the offset the follower should truncate to ensure
    *   accurate log replication.
    * - Finally truncate the logs for partitions in the truncating phase and mark the
    *   truncation complete. Do this within a lock to ensure no leadership changes can
    *   occur during truncation.
    */
  private def truncateToEpochEndOffsets(latestEpochsForPartitions: Map[TopicPartition, EpochData]): Unit = {
    val endOffsets = fetchEpochEndOffsets(latestEpochsForPartitions)
    //Ensure we hold a lock during truncation.
    inLock(partitionMapLock) {
      //Check no leadership and no leader epoch changes happened whilst we were unlocked, fetching epochs
      val epochEndOffsets = endOffsets.filter { case (tp, _) =>
        val curPartitionState = partitionStates.stateValue(tp)
        val partitionEpochRequest = latestEpochsForPartitions.getOrElse(tp, {
          throw new IllegalStateException(
            s"Leader replied with partition $tp not requested in OffsetsForLeaderEpoch request")
        })
        val leaderEpochInRequest = partitionEpochRequest.currentLeaderEpoch
        curPartitionState != null && leaderEpochInRequest == curPartitionState.currentLeaderEpoch
      }

      val ResultWithPartitions(fetchOffsets, partitionsWithError) = maybeTruncateToEpochEndOffsets(epochEndOffsets, latestEpochsForPartitions)
      handlePartitionsWithErrors(partitionsWithError, "truncateToEpochEndOffsets")
      updateFetchOffsetAndMaybeMarkTruncationComplete(fetchOffsets)
    }
  }

  // Visibility for unit tests
  protected[server] def truncateOnFetchResponse(epochEndOffsets: Map[TopicPartition, EpochEndOffset]): Unit = {
    inLock(partitionMapLock) {
      val ResultWithPartitions(fetchOffsets, partitionsWithError) = maybeTruncateToEpochEndOffsets(epochEndOffsets, Map.empty)
      handlePartitionsWithErrors(partitionsWithError, "truncateOnFetchResponse")
      updateFetchOffsetAndMaybeMarkTruncationComplete(fetchOffsets)
    }
  }

  // Visible for testing
  /**
   * 将日志截断到高水位处
   * @param partitions
   */
  private[server] def truncateToHighWatermark(partitions: Set[TopicPartition]): Unit = inLock(partitionMapLock) {
    val fetchOffsets = mutable.HashMap.empty[TopicPartition, OffsetTruncationState]

    // 遍历每个要执行截断操作的分区对象
    for (tp <- partitions) {
      // 获取分区读取状态
      val partitionState = partitionStates.stateValue(tp)
      if (partitionState != null) {
        // 取出高水位值，分区的最大可读取位移值就是高水位值
        val highWatermark = partitionState.fetchOffset
        val truncationState = OffsetTruncationState(highWatermark, truncationCompleted = true)

        info(s"Truncating partition $tp to local high watermark $highWatermark")
        // 执行将日志截断到高水位处
        if (doTruncate(tp, truncationState))
          fetchOffsets.put(tp, truncationState)
      }
    }

    // 更新这组分区的分区读取状态
    updateFetchOffsetAndMaybeMarkTruncationComplete(fetchOffsets)
  }

  private def maybeTruncateToEpochEndOffsets(fetchedEpochs: Map[TopicPartition, EpochEndOffset],
                                             latestEpochsForPartitions: Map[TopicPartition, EpochData]): ResultWithPartitions[Map[TopicPartition, OffsetTruncationState]] = {
    val fetchOffsets = mutable.HashMap.empty[TopicPartition, OffsetTruncationState]
    val partitionsWithError = mutable.HashSet.empty[TopicPartition]

    fetchedEpochs.forKeyValue { (tp, leaderEpochOffset) =>
      if (partitionStates.contains(tp)) {
        Errors.forCode(leaderEpochOffset.errorCode) match {
          case Errors.NONE =>
            val offsetTruncationState = getOffsetTruncationState(tp, leaderEpochOffset)
            if (doTruncate(tp, offsetTruncationState))
              fetchOffsets.put(tp, offsetTruncationState)

          case Errors.FENCED_LEADER_EPOCH =>
            val currentLeaderEpoch = latestEpochsForPartitions.get(tp)
              .map(epochEndOffset => Int.box(epochEndOffset.currentLeaderEpoch)).asJava
            if (onPartitionFenced(tp, currentLeaderEpoch))
              partitionsWithError += tp

          case error =>
            info(s"Retrying leaderEpoch request for partition $tp as the leader reported an error: $error")
            partitionsWithError += tp
        }
      } else {
        // Partitions may have been removed from the fetcher while the thread was waiting for fetch
        // response. Removed partitions are filtered out while holding `partitionMapLock` to ensure that we
        // don't update state for any partition that may have already been migrated to another thread.
        trace(s"Ignoring epoch offsets for partition $tp since it has been removed from this fetcher thread.")
      }
    }

    ResultWithPartitions(fetchOffsets, partitionsWithError)
  }

  /**
   * remove the partition if the partition state is NOT updated. Otherwise, keep the partition active.
   * @return true if the epoch in this thread is updated. otherwise, false
   */
  private def onPartitionFenced(tp: TopicPartition, requestEpoch: Optional[Integer]): Boolean = inLock(partitionMapLock) {
    Option(partitionStates.stateValue(tp)).exists { currentFetchState =>
      val currentLeaderEpoch = currentFetchState.currentLeaderEpoch
      if (requestEpoch.isPresent && requestEpoch.get == currentLeaderEpoch) {
        info(s"Partition $tp has an older epoch ($currentLeaderEpoch) than the current leader. Will await " +
          s"the new LeaderAndIsr state before resuming fetching.")
        markPartitionFailed(tp)
        false
      } else {
        info(s"Partition $tp has an new epoch ($currentLeaderEpoch) than the current leader. retry the partition later")
        true
      }
    }
  }

  /**
   * 发送 FETCH 请求给对应的 Leader 副本，并处理相应的 Response
   * @param sessionPartitions
   * @param fetchRequest
   */
  private def processFetchRequest(sessionPartitions: util.Map[TopicPartition, FetchRequest.PartitionData],
                                  fetchRequest: FetchRequest.Builder): Unit = {
    val partitionsWithError = mutable.Set[TopicPartition]()
    val divergingEndOffsets = mutable.Map.empty[TopicPartition, EpochEndOffset]
    var responseData: Map[TopicPartition, FetchData] = Map.empty

    try {
      trace(s"Sending fetch request $fetchRequest")
      // 给 Leader 发送 FETCH 请求
      responseData = fetchFromLeader(fetchRequest)
    } catch {
      case t: Throwable =>
        if (isRunning) {
          warn(s"Error in response for fetch request $fetchRequest", t)
          inLock(partitionMapLock) {
            partitionsWithError ++= partitionStates.partitionSet.asScala
            // there is an error occurred while fetching partitions, sleep a while
            // note that `AbstractFetcherThread.handlePartitionsWithError` will also introduce the same delay for every
            // partition with error effectively doubling the delay. It would be good to improve this.
            partitionMapCond.await(fetchBackOffMs, TimeUnit.MILLISECONDS)
          }
        }
    }
    // 更新请求发送速率指标
    fetcherStats.requestRate.mark()

    if (responseData.nonEmpty) {
      // process fetched data
      inLock(partitionMapLock) {
        responseData.forKeyValue { (topicPartition, partitionData) =>
          Option(partitionStates.stateValue(topicPartition)).foreach { currentFetchState =>
            // It's possible that a partition is removed and re-added or truncated when there is a pending fetch request.
            // In this case, we only want to process the fetch response if the partition state is ready for fetch and
            // the current offset is the same as the offset requested.
            // 获取分区核心信息
            val fetchPartitionData = sessionPartitions.get(topicPartition) 
            // 处理Response的条件：
            // 1. 数据不为空
            // 2. 要获取的位移值和之前已保存的下一条待获取位移值相等
            // 3. 当前分区处于可获取状态
            if (fetchPartitionData != null && fetchPartitionData.fetchOffset == currentFetchState.fetchOffset && currentFetchState.isReadyForFetch) {
              Errors.forCode(partitionData.errorCode) match {
                // 没有错误
                case Errors.NONE =>
                  try {
                    // Once we hand off the partition data to the subclass, we can't mess with it any more in this thread
                    // 交由子类完成 Response 的处理
                    val logAppendInfoOpt = processPartitionData(topicPartition, currentFetchState.fetchOffset,
                      partitionData)

                    logAppendInfoOpt.foreach { logAppendInfo =>
                      val validBytes = logAppendInfo.validBytes
                      val nextOffset = if (validBytes > 0) logAppendInfo.lastOffset + 1 else currentFetchState.fetchOffset
                      val lag = Math.max(0L, partitionData.highWatermark - nextOffset)
                      fetcherLagStats.getAndMaybePut(topicPartition).lag = lag

                      // ReplicaDirAlterThread may have removed topicPartition from the partitionStates after processing the partition data
                      if (validBytes > 0 && partitionStates.contains(topicPartition)) {
                        // Update partitionStates only if there is no exception during processPartitionData
                        val newFetchState = PartitionFetchState(nextOffset, Some(lag),
                          currentFetchState.currentLeaderEpoch, state = Fetching,
                          logAppendInfo.lastLeaderEpoch)
                        // 将该分区放置在有序 Map 读取顺序的末尾，保证公平性
                        partitionStates.updateAndMoveToEnd(topicPartition, newFetchState)
                        fetcherStats.byteRate.mark(validBytes)
                      }
                    }
                    if (isTruncationOnFetchSupported) {
                      FetchResponse.divergingEpoch(partitionData).ifPresent { divergingEpoch =>
                        divergingEndOffsets += topicPartition -> new EpochEndOffset()
                          .setPartition(topicPartition.partition)
                          .setErrorCode(Errors.NONE.code)
                          .setLeaderEpoch(divergingEpoch.epoch)
                          .setEndOffset(divergingEpoch.endOffset)
                      }
                    }
                  } catch {
                    case ime@( _: CorruptRecordException | _: InvalidRecordException) =>
                      // we log the error and continue. This ensures two things
                      // 1. If there is a corrupt message in a topic partition, it does not bring the fetcher thread
                      //    down and cause other topic partition to also lag
                      // 2. If the message is corrupt due to a transient state in the log (truncation, partial writes
                      //    can cause this), we simply continue and should get fixed in the subsequent fetches
                      error(s"Found invalid messages during fetch for partition $topicPartition " +
                        s"offset ${currentFetchState.fetchOffset}", ime)
                      partitionsWithError += topicPartition
                    case e: KafkaStorageException =>
                      error(s"Error while processing data for partition $topicPartition " +
                        s"at offset ${currentFetchState.fetchOffset}", e)
                      markPartitionFailed(topicPartition)
                    case t: Throwable =>
                      // stop monitoring this partition and add it to the set of failed partitions
                      error(s"Unexpected error occurred while processing data for partition $topicPartition " +
                        s"at offset ${currentFetchState.fetchOffset}", t)
                      markPartitionFailed(topicPartition)
                  }
                // 如果读取位移值越界，通常是因为 Leader 发生变更  
                case Errors.OFFSET_OUT_OF_RANGE =>
                  // 调整越界，主要办法是做截断
                  if (handleOutOfRangeError(topicPartition, currentFetchState, fetchPartitionData.currentLeaderEpoch))
                    // 如果依然不能成功，则加入到出错分区列表
                    partitionsWithError += topicPartition
                // 如果 Leader Epoch 值比 Leader 所在 Broker 上的 Epoch 值要新
                case Errors.UNKNOWN_LEADER_EPOCH =>
                  debug(s"Remote broker has a smaller leader epoch for partition $topicPartition than " +
                    s"this replica's current leader epoch of ${currentFetchState.currentLeaderEpoch}.")
                  // 加入到出错分区列表
                  partitionsWithError += topicPartition
                // 如果 Leader Epoch 值比 Leader 所在 Broker 上的 Epoch 值要旧
                case Errors.FENCED_LEADER_EPOCH =>
                  if (onPartitionFenced(topicPartition, fetchPartitionData.currentLeaderEpoch))
                    partitionsWithError += topicPartition
                // 如果Leader发生变更
                case Errors.NOT_LEADER_OR_FOLLOWER =>
                  debug(s"Remote broker is not the leader for partition $topicPartition, which could indicate " +
                    "that the partition is being moved")
                  // 加入到出错分区列表
                  partitionsWithError += topicPartition

                case Errors.UNKNOWN_TOPIC_OR_PARTITION =>
                  warn(s"Received ${Errors.UNKNOWN_TOPIC_OR_PARTITION} from the leader for partition $topicPartition. " +
                       "This error may be returned transiently when the partition is being created or deleted, but it is not " +
                       "expected to persist.")
                  // 加入到出错分区列表
                  partitionsWithError += topicPartition

                case partitionError =>
                  error(s"Error for partition $topicPartition at offset ${currentFetchState.fetchOffset}", partitionError.exception)
                  // 加入到出错分区列表
                  partitionsWithError += topicPartition
              }
            }
          }
        }
      }
    }

    if (divergingEndOffsets.nonEmpty)
      truncateOnFetchResponse(divergingEndOffsets)
    if (partitionsWithError.nonEmpty) {
      // 处理出错分区列表
      handlePartitionsWithErrors(partitionsWithError, "processFetchRequest")
    }
  }

  /**
   * This is used to mark partitions for truncation in ReplicaAlterLogDirsThread after leader
   * offsets are known.
   */
  def markPartitionsForTruncation(topicPartition: TopicPartition, truncationOffset: Long): Unit = {
    partitionMapLock.lockInterruptibly()
    try {
      Option(partitionStates.stateValue(topicPartition)).foreach { state =>
        val newState = PartitionFetchState(math.min(truncationOffset, state.fetchOffset),
          state.lag, state.currentLeaderEpoch, state.delay, state = Truncating,
          lastFetchedEpoch = None)
        partitionStates.updateAndMoveToEnd(topicPartition, newState)
        partitionMapCond.signalAll()
      }
    } finally partitionMapLock.unlock()
  }

  private def markPartitionFailed(topicPartition: TopicPartition): Unit = {
    partitionMapLock.lock()
    try {
      failedPartitions.add(topicPartition)
      removePartitions(Set(topicPartition))
    } finally partitionMapLock.unlock()
    warn(s"Partition $topicPartition marked as failed")
  }

  /**
   * Returns initial partition fetch state based on current state and the provided `initialFetchState`.
   * From IBP 2.7 onwards, we can rely on truncation based on diverging data returned in fetch responses.
   * For older versions, we can skip the truncation step iff the leader epoch matches the existing epoch.
   */
  private def partitionFetchState(tp: TopicPartition, initialFetchState: InitialFetchState, currentState: PartitionFetchState): PartitionFetchState = {
    if (currentState != null && currentState.currentLeaderEpoch == initialFetchState.currentLeaderEpoch) {
      currentState
    } else if (initialFetchState.initOffset < 0) {
      fetchOffsetAndTruncate(tp, initialFetchState.currentLeaderEpoch)
    } else if (isTruncationOnFetchSupported) {
      // With old message format, `latestEpoch` will be empty and we use Truncating state
      // to truncate to high watermark.
      val lastFetchedEpoch = latestEpoch(tp)
      val state = if (lastFetchedEpoch.nonEmpty) Fetching else Truncating
      PartitionFetchState(initialFetchState.initOffset, None, initialFetchState.currentLeaderEpoch,
          state, lastFetchedEpoch)
    } else {
      PartitionFetchState(initialFetchState.initOffset, None, initialFetchState.currentLeaderEpoch,
        state = Truncating, lastFetchedEpoch = None)
    }
  }

  def addPartitions(initialFetchStates: Map[TopicPartition, InitialFetchState]): Set[TopicPartition] = {
    partitionMapLock.lockInterruptibly()
    try {
      failedPartitions.removeAll(initialFetchStates.keySet)

      initialFetchStates.forKeyValue { (tp, initialFetchState) =>
        val currentState = partitionStates.stateValue(tp)
        val updatedState = partitionFetchState(tp, initialFetchState, currentState)
        partitionStates.updateAndMoveToEnd(tp, updatedState)
      }

      partitionMapCond.signalAll()
      initialFetchStates.keySet
    } finally partitionMapLock.unlock()
  }

  /**
    * Loop through all partitions, updating their fetch offset and maybe marking them as
    * truncation completed if their offsetTruncationState indicates truncation completed
    *
    * @param fetchOffsets the partitions to update fetch offset and maybe mark truncation complete
    */
  private def updateFetchOffsetAndMaybeMarkTruncationComplete(fetchOffsets: Map[TopicPartition, OffsetTruncationState]): Unit = {
    val newStates: Map[TopicPartition, PartitionFetchState] = partitionStates.partitionStateMap.asScala
      .map { case (topicPartition, currentFetchState) =>
        val maybeTruncationComplete = fetchOffsets.get(topicPartition) match {
          case Some(offsetTruncationState) =>
            val lastFetchedEpoch = latestEpoch(topicPartition)
            val state = if (isTruncationOnFetchSupported || offsetTruncationState.truncationCompleted)
              Fetching
            else
              Truncating
            PartitionFetchState(offsetTruncationState.offset, currentFetchState.lag,
              currentFetchState.currentLeaderEpoch, currentFetchState.delay, state, lastFetchedEpoch)
          case None => currentFetchState
        }
        (topicPartition, maybeTruncationComplete)
      }
    partitionStates.set(newStates.asJava)
  }

  /**
   * Called from ReplicaFetcherThread and ReplicaAlterLogDirsThread maybeTruncate for each topic
   * partition. Returns truncation offset and whether this is the final offset to truncate to
   *
   * For each topic partition, the offset to truncate to is calculated based on leader's returned
   * epoch and offset:
   *  -- If the leader replied with undefined epoch offset, we must use the high watermark. This can
   *  happen if 1) the leader is still using message format older than KAFKA_0_11_0; 2) the follower
   *  requested leader epoch < the first leader epoch known to the leader.
   *  -- If the leader replied with the valid offset but undefined leader epoch, we truncate to
   *  leader's offset if it is lower than follower's Log End Offset. This may happen if the
   *  leader is on the inter-broker protocol version < KAFKA_2_0_IV0
   *  -- If the leader replied with leader epoch not known to the follower, we truncate to the
   *  end offset of the largest epoch that is smaller than the epoch the leader replied with, and
   *  send OffsetsForLeaderEpochRequest with that leader epoch. In a more rare case, where the
   *  follower was not tracking epochs smaller than the epoch the leader replied with, we
   *  truncate the leader's offset (and do not send any more leader epoch requests).
   *  -- Otherwise, truncate to min(leader's offset, end offset on the follower for epoch that
   *  leader replied with, follower's Log End Offset).
   *
   * @param tp                    Topic partition
   * @param leaderEpochOffset     Epoch end offset received from the leader for this topic partition
   */
  private def getOffsetTruncationState(tp: TopicPartition,
                                       leaderEpochOffset: EpochEndOffset): OffsetTruncationState = inLock(partitionMapLock) {
    if (leaderEpochOffset.endOffset == UNDEFINED_EPOCH_OFFSET) {
      // truncate to initial offset which is the high watermark for follower replica. For
      // future replica, it is either high watermark of the future replica or current
      // replica's truncation offset (when the current replica truncates, it forces future
      // replica's partition state to 'truncating' and sets initial offset to its truncation offset)
      warn(s"Based on replica's leader epoch, leader replied with an unknown offset in $tp. " +
           s"The initial fetch offset ${partitionStates.stateValue(tp).fetchOffset} will be used for truncation.")
      OffsetTruncationState(partitionStates.stateValue(tp).fetchOffset, truncationCompleted = true)
    } else if (leaderEpochOffset.leaderEpoch == UNDEFINED_EPOCH) {
      // either leader or follower or both use inter-broker protocol version < KAFKA_2_0_IV0
      // (version 0 of OffsetForLeaderEpoch request/response)
      warn(s"Leader or replica is on protocol version where leader epoch is not considered in the OffsetsForLeaderEpoch response. " +
           s"The leader's offset ${leaderEpochOffset.endOffset} will be used for truncation in $tp.")
      OffsetTruncationState(min(leaderEpochOffset.endOffset, logEndOffset(tp)), truncationCompleted = true)
    } else {
      val replicaEndOffset = logEndOffset(tp)

      // get (leader epoch, end offset) pair that corresponds to the largest leader epoch
      // less than or equal to the requested epoch.
      endOffsetForEpoch(tp, leaderEpochOffset.leaderEpoch) match {
        case Some(OffsetAndEpoch(followerEndOffset, followerEpoch)) =>
          if (followerEpoch != leaderEpochOffset.leaderEpoch) {
            // the follower does not know about the epoch that leader replied with
            // we truncate to the end offset of the largest epoch that is smaller than the
            // epoch the leader replied with, and send another offset for leader epoch request
            val intermediateOffsetToTruncateTo = min(followerEndOffset, replicaEndOffset)
            info(s"Based on replica's leader epoch, leader replied with epoch ${leaderEpochOffset.leaderEpoch} " +
              s"unknown to the replica for $tp. " +
              s"Will truncate to $intermediateOffsetToTruncateTo and send another leader epoch request to the leader.")
            OffsetTruncationState(intermediateOffsetToTruncateTo, truncationCompleted = false)
          } else {
            val offsetToTruncateTo = min(followerEndOffset, leaderEpochOffset.endOffset)
            OffsetTruncationState(min(offsetToTruncateTo, replicaEndOffset), truncationCompleted = true)
          }
        case None =>
          // This can happen if the follower was not tracking leader epochs at that point (before the
          // upgrade, or if this broker is new). Since the leader replied with epoch <
          // requested epoch from follower, so should be safe to truncate to leader's
          // offset (this is the same behavior as post-KIP-101 and pre-KIP-279)
          warn(s"Based on replica's leader epoch, leader replied with epoch ${leaderEpochOffset.leaderEpoch} " +
            s"below any replica's tracked epochs for $tp. " +
            s"The leader's offset only ${leaderEpochOffset.endOffset} will be used for truncation.")
          OffsetTruncationState(min(leaderEpochOffset.endOffset, replicaEndOffset), truncationCompleted = true)
      }
    }
  }

  /**
   * Handle the out of range error. Return false if
   * 1) the request succeeded or
   * 2) was fenced and this thread haven't received new epoch,
   * which means we need not backoff and retry. True if there was a retriable error.
   */
  private def handleOutOfRangeError(topicPartition: TopicPartition,
                                    fetchState: PartitionFetchState,
                                    requestEpoch: Optional[Integer]): Boolean = {
    try {
      val newFetchState = fetchOffsetAndTruncate(topicPartition, fetchState.currentLeaderEpoch)
      partitionStates.updateAndMoveToEnd(topicPartition, newFetchState)
      info(s"Current offset ${fetchState.fetchOffset} for partition $topicPartition is " +
        s"out of range, which typically implies a leader change. Reset fetch offset to ${newFetchState.fetchOffset}")
      false
    } catch {
      case _: FencedLeaderEpochException =>
        onPartitionFenced(topicPartition, requestEpoch)

      case e @ (_ : UnknownTopicOrPartitionException |
                _ : UnknownLeaderEpochException |
                _ : NotLeaderOrFollowerException) =>
        info(s"Could not fetch offset for $topicPartition due to error: ${e.getMessage}")
        true

      case e: Throwable =>
        error(s"Error getting offset for partition $topicPartition", e)
        true
    }
  }

  /**
   * Handle a partition whose offset is out of range and return a new fetch offset.
   */
  protected def fetchOffsetAndTruncate(topicPartition: TopicPartition, currentLeaderEpoch: Int): PartitionFetchState = {
    val replicaEndOffset = logEndOffset(topicPartition)

    /**
     * Unclean leader election: A follower goes down, in the meanwhile the leader keeps appending messages. The follower comes back up
     * and before it has completely caught up with the leader's logs, all replicas in the ISR go down. The follower is now uncleanly
     * elected as the new leader, and it starts appending messages from the client. The old leader comes back up, becomes a follower
     * and it may discover that the current leader's end offset is behind its own end offset.
     *
     * In such a case, truncate the current follower's log to the current leader's end offset and continue fetching.
     *
     * There is a potential for a mismatch between the logs of the two replicas here. We don't fix this mismatch as of now.
     */
    val leaderEndOffset = fetchLatestOffsetFromLeader(topicPartition, currentLeaderEpoch)
    if (leaderEndOffset < replicaEndOffset) {
      warn(s"Reset fetch offset for partition $topicPartition from $replicaEndOffset to current " +
        s"leader's latest offset $leaderEndOffset")
      truncate(topicPartition, OffsetTruncationState(leaderEndOffset, truncationCompleted = true))

      fetcherLagStats.getAndMaybePut(topicPartition).lag = 0
      PartitionFetchState(leaderEndOffset, Some(0), currentLeaderEpoch,
        state = Fetching, lastFetchedEpoch = latestEpoch(topicPartition))
    } else {
      /**
       * If the leader's log end offset is greater than the follower's log end offset, there are two possibilities:
       * 1. The follower could have been down for a long time and when it starts up, its end offset could be smaller than the leader's
       * start offset because the leader has deleted old logs (log.logEndOffset < leaderStartOffset).
       * 2. When unclean leader election occurs, it is possible that the old leader's high watermark is greater than
       * the new leader's log end offset. So when the old leader truncates its offset to its high watermark and starts
       * to fetch from the new leader, an OffsetOutOfRangeException will be thrown. After that some more messages are
       * produced to the new leader. While the old leader is trying to handle the OffsetOutOfRangeException and query
       * the log end offset of the new leader, the new leader's log end offset becomes higher than the follower's log end offset.
       *
       * In the first case, the follower's current log end offset is smaller than the leader's log start offset. So the
       * follower should truncate all its logs, roll out a new segment and start to fetch from the current leader's log
       * start offset.
       * In the second case, the follower should just keep the current log segments and retry the fetch. In the second
       * case, there will be some inconsistency of data between old and new leader. We are not solving it here.
       * If users want to have strong consistency guarantees, appropriate configurations needs to be set for both
       * brokers and producers.
       *
       * Putting the two cases together, the follower should fetch from the higher one of its replica log end offset
       * and the current leader's log start offset.
       */
      val leaderStartOffset = fetchEarliestOffsetFromLeader(topicPartition, currentLeaderEpoch)
      warn(s"Reset fetch offset for partition $topicPartition from $replicaEndOffset to current " +
        s"leader's start offset $leaderStartOffset")
      val offsetToFetch = Math.max(leaderStartOffset, replicaEndOffset)
      // Only truncate log when current leader's log start offset is greater than follower's log end offset.
      if (leaderStartOffset > replicaEndOffset)
        truncateFullyAndStartAt(topicPartition, leaderStartOffset)

      val initialLag = leaderEndOffset - offsetToFetch
      fetcherLagStats.getAndMaybePut(topicPartition).lag = initialLag
      PartitionFetchState(offsetToFetch, Some(initialLag), currentLeaderEpoch,
        state = Fetching, lastFetchedEpoch = latestEpoch(topicPartition))
    }
  }

  def delayPartitions(partitions: Iterable[TopicPartition], delay: Long): Unit = {
    partitionMapLock.lockInterruptibly()
    try {
      for (partition <- partitions) {
        // 获取分区对应的分区状态
        Option(partitionStates.stateValue(partition)).foreach { currentFetchState =>
          // 如果分区状态不是延迟处理
          if (!currentFetchState.isDelayed) {
            // 将分区放入集合末尾，实现延时重试
            partitionStates.updateAndMoveToEnd(partition, PartitionFetchState(currentFetchState.fetchOffset,
              currentFetchState.lag, currentFetchState.currentLeaderEpoch, Some(new DelayedItem(delay)),
              currentFetchState.state, currentFetchState.lastFetchedEpoch))
          }
        }
      }
      partitionMapCond.signalAll()
    } finally partitionMapLock.unlock()
  }

  def removePartitions(topicPartitions: Set[TopicPartition]): Map[TopicPartition, PartitionFetchState] = {
    partitionMapLock.lockInterruptibly()
    try {
      topicPartitions.map { topicPartition =>
        val state = partitionStates.stateValue(topicPartition)
        partitionStates.remove(topicPartition)
        fetcherLagStats.unregister(topicPartition)
        topicPartition -> state
      }.filter(_._2 != null).toMap
    } finally partitionMapLock.unlock()
  }

  def partitionCount: Int = {
    partitionMapLock.lockInterruptibly()
    try partitionStates.size
    finally partitionMapLock.unlock()
  }

  def partitions: Set[TopicPartition] = {
    partitionMapLock.lockInterruptibly()
    try partitionStates.partitionSet.asScala.toSet
    finally partitionMapLock.unlock()
  }

  // Visible for testing
  private[server] def fetchState(topicPartition: TopicPartition): Option[PartitionFetchState] = inLock(partitionMapLock) {
    Option(partitionStates.stateValue(topicPartition))
  }

  protected def toMemoryRecords(records: Records): MemoryRecords = {
    (records: @unchecked) match {
      case r: MemoryRecords => r
      case r: FileRecords =>
        val buffer = ByteBuffer.allocate(r.sizeInBytes)
        r.readInto(buffer, 0)
        MemoryRecords.readableRecords(buffer)
    }
  }
}

object AbstractFetcherThread {

  case class ReplicaFetch(partitionData: util.Map[TopicPartition, FetchRequest.PartitionData], fetchRequest: FetchRequest.Builder)
  case class ResultWithPartitions[R](result: R, partitionsWithError: Set[TopicPartition])

}

object FetcherMetrics {
  val ConsumerLag = "ConsumerLag"
  val RequestsPerSec = "RequestsPerSec"
  val BytesPerSec = "BytesPerSec"
}

class FetcherLagMetrics(metricId: ClientIdTopicPartition) extends KafkaMetricsGroup {

  private[this] val lagVal = new AtomicLong(-1L)
  private[this] val tags = Map(
    "clientId" -> metricId.clientId,
    "topic" -> metricId.topicPartition.topic,
    "partition" -> metricId.topicPartition.partition.toString)

  newGauge(FetcherMetrics.ConsumerLag, () => lagVal.get, tags)

  def lag_=(newLag: Long): Unit = {
    lagVal.set(newLag)
  }

  def lag = lagVal.get

  def unregister(): Unit = {
    removeMetric(FetcherMetrics.ConsumerLag, tags)
  }
}

class FetcherLagStats(metricId: ClientIdAndBroker) {
  private val valueFactory = (k: TopicPartition) => new FetcherLagMetrics(ClientIdTopicPartition(metricId.clientId, k))
  val stats = new Pool[TopicPartition, FetcherLagMetrics](Some(valueFactory))

  def getAndMaybePut(topicPartition: TopicPartition): FetcherLagMetrics = {
    stats.getAndMaybePut(topicPartition)
  }

  def unregister(topicPartition: TopicPartition): Unit = {
    val lagMetrics = stats.remove(topicPartition)
    if (lagMetrics != null) lagMetrics.unregister()
  }

  def unregister(): Unit = {
    stats.keys.toBuffer.foreach { key: TopicPartition =>
      unregister(key)
    }
  }
}

class FetcherStats(metricId: ClientIdAndBroker) extends KafkaMetricsGroup {
  val tags = Map("clientId" -> metricId.clientId,
    "brokerHost" -> metricId.brokerHost,
    "brokerPort" -> metricId.brokerPort.toString)

  val requestRate = newMeter(FetcherMetrics.RequestsPerSec, "requests", TimeUnit.SECONDS, tags)

  val byteRate = newMeter(FetcherMetrics.BytesPerSec, "bytes", TimeUnit.SECONDS, tags)

  def unregister(): Unit = {
    removeMetric(FetcherMetrics.RequestsPerSec, tags)
    removeMetric(FetcherMetrics.BytesPerSec, tags)
  }

}

case class ClientIdTopicPartition(clientId: String, topicPartition: TopicPartition) {
  override def toString: String = s"$clientId-$topicPartition"
}

/**
 * 表示副本读取状态，目前共有截断中和获取中两种状态
 */
sealed trait ReplicaState

/**
 * 副本截断中
 */
case object Truncating extends ReplicaState

/**
 * 副本获取中
 */
case object Fetching extends ReplicaState

object PartitionFetchState {
  def apply(offset: Long, lag: Option[Long], currentLeaderEpoch: Int, state: ReplicaState,
            lastFetchedEpoch: Option[Int]): PartitionFetchState = {
    PartitionFetchState(offset, lag, currentLeaderEpoch, None, state, lastFetchedEpoch)
  }
}


/**
 * case class to keep partition offset and its state(truncatingLog, delayed)
 * This represents a partition as being either:
 * (1) Truncating its log, for example having recently become a follower
 * (2) Delayed, for example due to an error, where we subsequently back off a bit
 * (3) ReadyForFetch, the is the active state where the thread is actively fetching data.
 * 
 * 分区读取状态类，保存分区的已读取位移值和对应的副本状态
 *
 * @param fetchOffset
 * @param lag
 * @param currentLeaderEpoch
 * @param delay
 * @param state 副本状态
 * @param lastFetchedEpoch
 */
case class PartitionFetchState(fetchOffset: Long,
                               lag: Option[Long],
                               currentLeaderEpoch: Int,
                               delay: Option[DelayedItem],
                               state: ReplicaState,
                               lastFetchedEpoch: Option[Int]) {
  // 分区的状态比副本的状态要更严格一些，二者的状态并不是一一对应的

  /**
   * 分区可获取状态，满足的条件是副本处于 Fetching 状态且未被推迟执行
   *
   * @return
   */
  def isReadyForFetch: Boolean = state == Fetching && !isDelayed

  /**
   * 副本处于 ISR 状态，满足条件是没有 lag
   *
   * @return
   */
  def isReplicaInSync: Boolean = lag.isDefined && lag.get <= 0

  /**
   * 分区截断中状态，满足条件是副本处于 Truncating 状态且未被推迟执行
   *
   * 该方法常用于副本限流，比较少见
   *
   * @return
   */
  def isTruncating: Boolean = state == Truncating && !isDelayed

  /**
   * 分区被推迟状态，满足的条件是存在未过期的延迟任务
   *
   * 该方法用于判断是否需要推迟获取对应分区的消息
   *
   * @return
   */
  def isDelayed: Boolean = delay.exists(_.getDelay(TimeUnit.MILLISECONDS) > 0)

  override def toString: String = {
    s"FetchState(fetchOffset=$fetchOffset" +
      s", currentLeaderEpoch=$currentLeaderEpoch" +
      s", lastFetchedEpoch=$lastFetchedEpoch" +
      s", state=$state" +
      s", lag=$lag" +
      s", delay=${delay.map(_.delayMs).getOrElse(0)}ms" +
      s")"
  }
}

/**
 * OffsetTruncationState 类封装了一个位移值和一个截断完成与否的布尔值状态，
 * 它的主要作用是告诉 Kafka 要把指定分区下副本截断到哪个位移值。
 * @param offset 位移值
 * @param truncationCompleted 截断是否完成
 */
case class OffsetTruncationState(offset: Long, truncationCompleted: Boolean) {

  def this(offset: Long) = this(offset, true)

  override def toString: String = "offset:%d-truncationCompleted:%b".format(offset, truncationCompleted)
}

case class OffsetAndEpoch(offset: Long, leaderEpoch: Int) {
  override def toString: String = {
    s"(offset=$offset, leaderEpoch=$leaderEpoch)"
  }
}
