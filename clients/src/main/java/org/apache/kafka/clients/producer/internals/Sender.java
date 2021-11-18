/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.producer.internals;

import org.apache.kafka.clients.ApiVersions;
import org.apache.kafka.clients.ClientRequest;
import org.apache.kafka.clients.ClientResponse;
import org.apache.kafka.clients.KafkaClient;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.NetworkClientUtils;
import org.apache.kafka.clients.RequestCompletionHandler;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.ClusterAuthorizationException;
import org.apache.kafka.common.errors.InvalidMetadataException;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.apache.kafka.common.errors.TransactionAbortedException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.apache.kafka.common.message.ProduceRequestData;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.metrics.stats.Avg;
import org.apache.kafka.common.metrics.stats.Max;
import org.apache.kafka.common.metrics.stats.Meter;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.record.RecordBatch;
import org.apache.kafka.common.requests.AbstractRequest;
import org.apache.kafka.common.requests.FindCoordinatorRequest;
import org.apache.kafka.common.requests.ProduceRequest;
import org.apache.kafka.common.requests.ProduceResponse;
import org.apache.kafka.common.requests.RequestHeader;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The background thread that handles the sending of produce requests to the Kafka cluster. This thread makes metadata
 * requests to renew its view of the cluster and then sends produce requests to the appropriate nodes.
 */
public class Sender implements Runnable {

    private final Logger log;

    /* the state of each nodes connection */
    // Kafka网络通信客户端，主要封装与broker的网络通信
    private final KafkaClient client;

    /* the record accumulator that batches records */
    // 消息记录积累器，消息追加的入口（RecordAccumulator的append方法）
    private final RecordAccumulator accumulator;

    /* the metadata for the client */
    // 元数据管理器，即topic的路由分区信息
    private final ProducerMetadata metadata;

    /* the flag indicating whether the producer should guarantee the message order on the broker or not. */
    // 是否需要保证消息的顺序性
    private final boolean guaranteeMessageOrder;

    /* the maximum request size to attempt to send to the server */
    // 调用send方法发送的最大请求大小，包括key、消息体序列化后的消息总大小都不能超过该值，
    // 通过「max.request.size」设置
    private final int maxRequestSize;

    /* the number of acknowledgements to request from the server */
    // 定义消息"已提交"的条件，即broker向客户端承诺已提交的条件，
    // 可选值：0、1、-1
    private final short acks;

    /* the number of times to retry a failed request before giving up */
    // 重试次数
    private final int retries;

    /* the clock instance used for getting the time */
    // 时间工具类
    private final Time time;

    /* true while the sender thread is still running */
    // 线程状态，true表示运行中
    private volatile boolean running;

    /* true when the caller wants to ignore all unsent/inflight messages and force close.  */
    // 是否强制关闭，若为true则会忽略正在发送中的消息
    private volatile boolean forceClose;

    /* metrics */
    // 消息发送相关的统计指标收集器
    private final SenderMetrics sensors;

    /* the max time to wait for the server to respond to the request*/
    // 请求的超时时间
    private final int requestTimeoutMs;

    /* The max time to wait before retrying a request which has failed */
    // 请求失败时在重试之前等待的时间
    private final long retryBackoffMs;

    /* current request API versions supported by the known brokers */
    // api版本信息
    private final ApiVersions apiVersions;

    /* all the state related to transactions, in particular the producer id, producer epoch, and sequence numbers */
    // tx处理器
    private final TransactionManager transactionManager;

    // A per-partition queue of batches ordered by creation time for tracking the in-flight batches
    // 存储待发送的消息批次
    private final Map<TopicPartition, List<ProducerBatch>> inFlightBatches;

    public Sender(LogContext logContext,
                  KafkaClient client,
                  ProducerMetadata metadata,
                  RecordAccumulator accumulator,
                  boolean guaranteeMessageOrder,
                  int maxRequestSize,
                  short acks,
                  int retries,
                  SenderMetricsRegistry metricsRegistry,
                  Time time,
                  int requestTimeoutMs,
                  long retryBackoffMs,
                  TransactionManager transactionManager,
                  ApiVersions apiVersions) {
        this.log = logContext.logger(Sender.class);
        this.client = client;
        this.accumulator = accumulator;
        this.metadata = metadata;
        this.guaranteeMessageOrder = guaranteeMessageOrder;
        this.maxRequestSize = maxRequestSize;
        this.running = true;
        this.acks = acks;
        this.retries = retries;
        this.time = time;
        this.sensors = new SenderMetrics(metricsRegistry, metadata, client, time);
        this.requestTimeoutMs = requestTimeoutMs;
        this.retryBackoffMs = retryBackoffMs;
        this.apiVersions = apiVersions;
        this.transactionManager = transactionManager;
        this.inFlightBatches = new HashMap<>();
    }

    public List<ProducerBatch> inFlightBatches(TopicPartition tp) {
        return inFlightBatches.containsKey(tp) ? inFlightBatches.get(tp) : new ArrayList<>();
    }

    private void maybeRemoveFromInflightBatches(ProducerBatch batch) {
        List<ProducerBatch> batches = inFlightBatches.get(batch.topicPartition);
        if (batches != null) {
            batches.remove(batch);
            if (batches.isEmpty()) {
                inFlightBatches.remove(batch.topicPartition);
            }
        }
    }

    private void maybeRemoveAndDeallocateBatch(ProducerBatch batch) {
        maybeRemoveFromInflightBatches(batch);
        this.accumulator.deallocate(batch);
    }

    /**
     *  Get the in-flight batches that has reached delivery timeout.
     */
    private List<ProducerBatch> getExpiredInflightBatches(long now) {
        List<ProducerBatch> expiredBatches = new ArrayList<>();

        for (Iterator<Map.Entry<TopicPartition, List<ProducerBatch>>> batchIt = inFlightBatches.entrySet().iterator(); batchIt.hasNext();) {
            Map.Entry<TopicPartition, List<ProducerBatch>> entry = batchIt.next();
            List<ProducerBatch> partitionInFlightBatches = entry.getValue();
            if (partitionInFlightBatches != null) {
                Iterator<ProducerBatch> iter = partitionInFlightBatches.iterator();
                while (iter.hasNext()) {
                    ProducerBatch batch = iter.next();
                    if (batch.hasReachedDeliveryTimeout(accumulator.getDeliveryTimeoutMs(), now)) {
                        iter.remove();
                        // expireBatches is called in Sender.sendProducerData, before client.poll.
                        // The !batch.isDone() invariant should always hold. An IllegalStateException
                        // exception will be thrown if the invariant is violated.
                        if (!batch.isDone()) {
                            expiredBatches.add(batch);
                        } else {
                            throw new IllegalStateException(batch.topicPartition + " batch created at " +
                                batch.createdMs + " gets unexpected final state " + batch.finalState());
                        }
                    } else {
                        accumulator.maybeUpdateNextBatchExpiryTime(batch);
                        break;
                    }
                }
                if (partitionInFlightBatches.isEmpty()) {
                    batchIt.remove();
                }
            }
        }
        return expiredBatches;
    }

    private void addToInflightBatches(List<ProducerBatch> batches) {
        for (ProducerBatch batch : batches) {
            List<ProducerBatch> inflightBatchList = inFlightBatches.get(batch.topicPartition);
            if (inflightBatchList == null) {
                inflightBatchList = new ArrayList<>();
                inFlightBatches.put(batch.topicPartition, inflightBatchList);
            }
            inflightBatchList.add(batch);
        }
    }

    public void addToInflightBatches(Map<Integer, List<ProducerBatch>> batches) {
        for (List<ProducerBatch> batchList : batches.values()) {
            addToInflightBatches(batchList);
        }
    }

    private boolean hasPendingTransactionalRequests() {
        return transactionManager != null && transactionManager.hasPendingRequests() && transactionManager.hasOngoingTransaction();
    }

    /**
     * The main run loop for the sender thread
     */
    @Override
    public void run() {
        log.debug("Starting Kafka producer I/O thread.");

        // main loop, runs until close is called
        while (running) {
            try {
                // Sender线程在运行状态下主要的业务处理方法，将消息缓冲区中的消息向broker发送
                runOnce();
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        log.debug("Beginning shutdown of Kafka producer I/O thread, sending remaining records.");

        // okay we stopped accepting requests but there may still be
        // requests in the transaction manager, accumulator or waiting for acknowledgment,
        // wait until these are completed.
        // 如果主动关闭Sender线程，且非强制关闭，则如果缓冲区还有消息待发送，
        // 将会再次调用runOnce()方法将剩余的消息发送完毕后再退出
        while (!forceClose && ((this.accumulator.hasUndrained() || this.client.inFlightRequestCount() > 0) || hasPendingTransactionalRequests())) {
            try {
                runOnce();
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        // Abort the transaction if any commit or abort didn't go through the transaction manager's queue
        while (!forceClose && transactionManager != null && transactionManager.hasOngoingTransaction()) {
            if (!transactionManager.isCompleting()) {
                log.info("Aborting incomplete transaction due to shutdown");
                transactionManager.beginAbort();
            }
            try {
                runOnce();
            } catch (Exception e) {
                log.error("Uncaught error in kafka producer I/O thread: ", e);
            }
        }

        // 如果强制关闭Sender线程，则会拒绝未完成提交的消息
        if (forceClose) {
            // We need to fail all the incomplete transactional requests and batches and wake up the threads waiting on
            // the futures.
            if (transactionManager != null) {
                log.debug("Aborting incomplete transactional requests due to forced shutdown");
                transactionManager.close();
            }
            log.debug("Aborting incomplete batches due to forced shutdown");
            this.accumulator.abortIncompleteBatches();
        }
        try {
            // 关闭Kafka Client（i.e.网络通信的对象）
            this.client.close();
        } catch (Exception e) {
            log.error("Failed to close network client", e);
        }

        log.debug("Shutdown of Kafka producer I/O thread has completed.");
    }

    /**
     * Run a single iteration of sending
     *
     */
    void runOnce() {
        if (transactionManager != null) {
            try {
                transactionManager.maybeResolveSequences();

                // do not continue sending if the transaction manager is in a failed state
                if (transactionManager.hasFatalError()) {
                    RuntimeException lastError = transactionManager.lastError();
                    if (lastError != null)
                        maybeAbortBatches(lastError);
                    client.poll(retryBackoffMs, time.milliseconds());
                    return;
                }

                // Check whether we need a new producerId. If so, we will enqueue an InitProducerId
                // request which will be sent below
                transactionManager.bumpIdempotentEpochAndResetIdIfNeeded();

                if (maybeSendAndPollTransactionalRequest()) {
                    return;
                }
            } catch (AuthenticationException e) {
                // This is already logged as error, but propagated here to perform any clean ups.
                log.trace("Authentication exception while processing transactional request", e);
                transactionManager.authenticationFailed(e);
            }
        }

        long currentTimeMs = time.milliseconds();
        // 调用sendProducerData方法发送消息
        long pollTimeout = sendProducerData(currentTimeMs);
        client.poll(pollTimeout, currentTimeMs);
    }

    private long sendProducerData(long now) {
        Cluster cluster = metadata.fetch();
        // get the list of partitions with data ready to send
        // 根据当前时间，并根据缓存队列中的数据判断哪些topic的哪些分区以及达到发送条件
        RecordAccumulator.ReadyCheckResult result = this.accumulator.ready(cluster, now);

        // if there are any partitions whose leaders are not known yet, force metadata update
        // 如果在待发送的消息中未找到其路由信息，则需要首先去broker服务器中拉取对应的路由信息（i.e.分区leader的节点信息）
        if (!result.unknownLeaderTopics.isEmpty()) {
            // The set of topics with unknown leader contains topics with leader election pending as well as
            // topics which may have expired. Add the topic again to metadata to ensure it is included
            // and request metadata update, since there are messages to send to the topic.
            for (String topic : result.unknownLeaderTopics)
                this.metadata.add(topic, now);

            log.debug("Requesting metadata update due to unknown leader topics from the batched records: {}",
                result.unknownLeaderTopics);
            this.metadata.requestUpdate();
        }

        // remove any nodes we aren't ready to send to
        // 移除在网络层面还没有准备好的分区，并计算在接下来多久的时间间隔内，该分区都将处于未准备状态
        Iterator<Node> iter = result.readyNodes.iterator();
        // notReady等待时间默认是「Long.MAX_VALUE」
        long notReadyTimeout = Long.MAX_VALUE;
        while (iter.hasNext()) {
            Node node = iter.next();
            // 网络环境准备好的条件：
            // 1）分区不存在未完成的更新metadata的请求
            // 2）当前生产者与对端broker已建立连接并完成了TCP三次挥手
            // 3）若启用SSL、ACL等机制，相关状态都准备就绪
            // 4）该分区对应的连接正在处理中的请求没有超过默认值，默认为5，可通过属性「max.in.flight.requests.per.connection」来设置
            if (!this.client.ready(node, now)) {
                iter.remove();
                // 更新notReadyTimeout，client.pollDelayMs 预估分区在接下来多久的时间间隔内都将处于「非ready」状态
                // 标准如下：
                // 1）如果与对端的TCP连接已建立好，并处于已连接状态，此时如果没有触发限流，则返回0，如果有触发限流，则返回限流的等待时间
                // 2）如果与对端的TCP连接还未建立好，则返回Long.MAX_VALUE，因为连接建立好后，会唤醒发送线程的
                notReadyTimeout = Math.min(notReadyTimeout, this.client.pollDelayMs(node, now));
            }
        }

        // create produce requests
        // 根据已准备的分区，从缓存区中抽取待发送的消息批次（ProducerBatch），并且按照<nodeId,List>组织
        // 需要注意的是，抽取后的ProducerBatch将不能再追加消息了，即使还有剩余空间可用
        Map<Integer, List<ProducerBatch>> batches = this.accumulator.drain(cluster, result.readyNodes, this.maxRequestSize, now);
        // 将抽取的ProducerBatch加入到inflightBatches中（就是Sender的一个attr）
        // inflightBatches声明如下：
        // private final Map<TopicPartition, List<ProducerBatch>> inflightBatches;
        // 即按照topic分区为key，存储抽取的ProducerBatch，
        // 该属性的含义就是存储待发送的消息批次，可以根据该attr得知在消息发送时以分区为维度Sender线程的积压情况，
        //「max.in.flight.requests.per.connection」就是来控制积压的最大数量，如果积压达到这个数值，针对该队列的消息发送就会被限流
        addToInflightBatches(batches);
        if (guaranteeMessageOrder) {
            // Mute all the partitions drained
            for (List<ProducerBatch> batchList : batches.values()) {
                for (ProducerBatch batch : batchList)
                    this.accumulator.mutePartition(batch.topicPartition);
            }
        }

        // 从inflightBatches与batches中查找已过期的消息批次（ProducerBatch），
        // 判断是否过期的标准是系统当前时间和ProducerBatch创建时间之差是否超过120s
        // 过期时间可以通过参数「delivery.timeout.ms」设置
        accumulator.resetNextBatchExpiryTime();
        List<ProducerBatch> expiredInflightBatches = getExpiredInflightBatches(now);
        List<ProducerBatch> expiredBatches = this.accumulator.expiredBatches(now);
        expiredBatches.addAll(expiredInflightBatches);

        // Reset the producer id if an expired batch has previously been sent to the broker. Also update the metrics
        // for expired batches. see the documentation of @TransactionState.resetIdempotentProducerId to understand why
        // we need to reset the producer id here.
        // 处理已超时的消息批次，通知该批消息发送失败，
        // i.e.通过设置KafkaProducer#send方法返回的凭证中的「FutureRecordMetadata」里的「ProduceRequestResult result」，
        // 使之调用其get方法不会阻塞
        if (!expiredBatches.isEmpty())
            log.trace("Expired {} batches in accumulator", expiredBatches.size());
        for (ProducerBatch expiredBatch : expiredBatches) {
            String errorMessage = "Expiring " + expiredBatch.recordCount + " record(s) for " + expiredBatch.topicPartition
                + ":" + (now - expiredBatch.createdMs) + " ms has passed since batch creation";
            failBatch(expiredBatch, new TimeoutException(errorMessage), false);
            if (transactionManager != null && expiredBatch.inRetry()) {
                // This ensures that no new batches are drained until the current in flight batches are fully resolved.
                transactionManager.markSequenceUnresolved(expiredBatch);
            }
        }
        // 收集统计指标
        sensors.updateProduceRequestMetrics(batches);

        // If we have any nodes that are ready to send + have sendable data, poll with 0 timeout so this can immediately
        // loop and try sending more data. Otherwise, the timeout will be the smaller value between next batch expiry
        // time, and the delay time for checking data availability. Note that the nodes may have data that isn't yet
        // sendable due to lingering, backing off, etc. This specifically does not include nodes with sendable data
        // that aren't ready to send since they would cause busy looping.
        // 设置下一次的发送延时
        long pollTimeout = Math.min(result.nextReadyCheckDelayMs, notReadyTimeout);
        pollTimeout = Math.min(pollTimeout, this.accumulator.nextExpiryTimeMs() - now);
        pollTimeout = Math.max(pollTimeout, 0);
        if (!result.readyNodes.isEmpty()) {
            log.trace("Nodes with data ready to send: {}", result.readyNodes);
            // if some partitions are already ready to be sent, the select time would be 0;
            // otherwise if some partition already has some data accumulated but not ready yet,
            // the select time will be the time difference between now and its linger expiry time;
            // otherwise the select time will be the time difference between now and the metadata expiry time;
            pollTimeout = 0;
        }
        // 按照brokerId分别构建发送请求，注意只是构建，并没有触发真正的网络调用，最终会通过NetworkClient#send方法将这批数据设置到
        // NetworkClient的待发送数据中
        // i.e.每一个broker会将多个ProducerBatch一起封装成一个请求进行发送，在同一时间每一个与broker的连接只能发送一个请求
        sendProduceRequests(batches, now);
        return pollTimeout;
    }

    /**
     * Returns true if a transactional request is sent or polled, or if a FindCoordinator request is enqueued
     */
    private boolean maybeSendAndPollTransactionalRequest() {
        if (transactionManager.hasInFlightRequest()) {
            // as long as there are outstanding transactional requests, we simply wait for them to return
            client.poll(retryBackoffMs, time.milliseconds());
            return true;
        }

        if (transactionManager.hasAbortableError() || transactionManager.isAborting()) {
            if (accumulator.hasIncomplete()) {
                // Attempt to get the last error that caused this abort.
                RuntimeException exception = transactionManager.lastError();
                // If there was no error, but we are still aborting,
                // then this is most likely a case where there was no fatal error.
                if (exception == null) {
                    exception = new TransactionAbortedException();
                }
                accumulator.abortUndrainedBatches(exception);
            }
        }

        TransactionManager.TxnRequestHandler nextRequestHandler = transactionManager.nextRequest(accumulator.hasIncomplete());
        if (nextRequestHandler == null)
            return false;

        AbstractRequest.Builder<?> requestBuilder = nextRequestHandler.requestBuilder();
        Node targetNode = null;
        try {
            FindCoordinatorRequest.CoordinatorType coordinatorType = nextRequestHandler.coordinatorType();
            targetNode = coordinatorType != null ?
                    transactionManager.coordinator(coordinatorType) :
                    client.leastLoadedNode(time.milliseconds());
            if (targetNode != null) {
                if (!awaitNodeReady(targetNode, coordinatorType)) {
                    log.trace("Target node {} not ready within request timeout, will retry when node is ready.", targetNode);
                    maybeFindCoordinatorAndRetry(nextRequestHandler);
                    return true;
                }
            } else if (coordinatorType != null) {
                log.trace("Coordinator not known for {}, will retry {} after finding coordinator.", coordinatorType, requestBuilder.apiKey());
                maybeFindCoordinatorAndRetry(nextRequestHandler);
                return true;
            } else {
                log.trace("No nodes available to send requests, will poll and retry when until a node is ready.");
                transactionManager.retry(nextRequestHandler);
                client.poll(retryBackoffMs, time.milliseconds());
                return true;
            }

            if (nextRequestHandler.isRetry())
                time.sleep(nextRequestHandler.retryBackoffMs());

            long currentTimeMs = time.milliseconds();
            ClientRequest clientRequest = client.newClientRequest(targetNode.idString(), requestBuilder, currentTimeMs,
                true, requestTimeoutMs, nextRequestHandler);
            log.debug("Sending transactional request {} to node {} with correlation ID {}", requestBuilder, targetNode, clientRequest.correlationId());
            client.send(clientRequest, currentTimeMs);
            transactionManager.setInFlightCorrelationId(clientRequest.correlationId());
            client.poll(retryBackoffMs, time.milliseconds());
            return true;
        } catch (IOException e) {
            log.debug("Disconnect from {} while trying to send request {}. Going " +
                    "to back off and retry.", targetNode, requestBuilder, e);
            // We break here so that we pick up the FindCoordinator request immediately.
            maybeFindCoordinatorAndRetry(nextRequestHandler);
            return true;
        }
    }

    private void maybeFindCoordinatorAndRetry(TransactionManager.TxnRequestHandler nextRequestHandler) {
        if (nextRequestHandler.needsCoordinator()) {
            transactionManager.lookupCoordinator(nextRequestHandler);
        } else {
            // For non-coordinator requests, sleep here to prevent a tight loop when no node is available
            time.sleep(retryBackoffMs);
            metadata.requestUpdate();
        }

        transactionManager.retry(nextRequestHandler);
    }

    private void maybeAbortBatches(RuntimeException exception) {
        if (accumulator.hasIncomplete()) {
            log.error("Aborting producer batches due to fatal error", exception);
            accumulator.abortBatches(exception);
        }
    }

    /**
     * Start closing the sender (won't actually complete until all data is sent out)
     */
    public void initiateClose() {
        // Ensure accumulator is closed first to guarantee that no more appends are accepted after
        // breaking from the sender loop. Otherwise, we may miss some callbacks when shutting down.
        this.accumulator.close();
        this.running = false;
        this.wakeup();
    }

    /**
     * Closes the sender without sending out any pending messages.
     */
    public void forceClose() {
        this.forceClose = true;
        initiateClose();
    }

    public boolean isRunning() {
        return running;
    }

    private boolean awaitNodeReady(Node node, FindCoordinatorRequest.CoordinatorType coordinatorType) throws IOException {
        if (NetworkClientUtils.awaitReady(client, node, time, requestTimeoutMs)) {
            if (coordinatorType == FindCoordinatorRequest.CoordinatorType.TRANSACTION) {
                // Indicate to the transaction manager that the coordinator is ready, allowing it to check ApiVersions
                // This allows us to bump transactional epochs even if the coordinator is temporarily unavailable at
                // the time when the abortable error is handled
                transactionManager.handleCoordinatorReady();
            }
            return true;
        }
        return false;
    }

    /**
     * Handle a produce response
     */
    private void handleProduceResponse(ClientResponse response, Map<TopicPartition, ProducerBatch> batches, long now) {
        RequestHeader requestHeader = response.requestHeader();
        int correlationId = requestHeader.correlationId();
        if (response.wasDisconnected()) {
            log.trace("Cancelled request with header {} due to node {} being disconnected",
                requestHeader, response.destination());
            for (ProducerBatch batch : batches.values())
                completeBatch(batch, new ProduceResponse.PartitionResponse(Errors.NETWORK_EXCEPTION, String.format("Disconnected from node %s", response.destination())),
                        correlationId, now);
        } else if (response.versionMismatch() != null) {
            log.warn("Cancelled request {} due to a version mismatch with node {}",
                    response, response.destination(), response.versionMismatch());
            for (ProducerBatch batch : batches.values())
                completeBatch(batch, new ProduceResponse.PartitionResponse(Errors.UNSUPPORTED_VERSION), correlationId, now);
        } else {
            log.trace("Received produce response from node {} with correlation id {}", response.destination(), correlationId);
            // if we have a response, parse it
            if (response.hasResponse()) {
                // Sender should exercise PartitionProduceResponse rather than ProduceResponse.PartitionResponse
                // https://issues.apache.org/jira/browse/KAFKA-10696
                ProduceResponse produceResponse = (ProduceResponse) response.responseBody();
                produceResponse.data().responses().forEach(r -> r.partitionResponses().forEach(p -> {
                    TopicPartition tp = new TopicPartition(r.name(), p.index());
                    ProduceResponse.PartitionResponse partResp = new ProduceResponse.PartitionResponse(
                            Errors.forCode(p.errorCode()),
                            p.baseOffset(),
                            p.logAppendTimeMs(),
                            p.logStartOffset(),
                            p.recordErrors()
                                .stream()
                                .map(e -> new ProduceResponse.RecordError(e.batchIndex(), e.batchIndexErrorMessage()))
                                .collect(Collectors.toList()),
                            p.errorMessage());
                    ProducerBatch batch = batches.get(tp);
                    completeBatch(batch, partResp, correlationId, now);
                }));
                this.sensors.recordLatency(response.destination(), response.requestLatencyMs());
            } else {
                // this is the acks = 0 case, just complete all requests
                for (ProducerBatch batch : batches.values()) {
                    completeBatch(batch, new ProduceResponse.PartitionResponse(Errors.NONE), correlationId, now);
                }
            }
        }
    }

    /**
     * Complete or retry the given batch of records.
     *
     * @param batch The record batch
     * @param response The produce response
     * @param correlationId The correlation id for the request
     * @param now The current POSIX timestamp in milliseconds
     */
    private void completeBatch(ProducerBatch batch, ProduceResponse.PartitionResponse response, long correlationId,
                               long now) {
        Errors error = response.error;

        if (error == Errors.MESSAGE_TOO_LARGE && batch.recordCount > 1 && !batch.isDone() &&
                (batch.magic() >= RecordBatch.MAGIC_VALUE_V2 || batch.isCompressed())) {
            // If the batch is too large, we split the batch and send the split batches again. We do not decrement
            // the retry attempts in this case.
            log.warn(
                "Got error produce response in correlation id {} on topic-partition {}, splitting and retrying ({} attempts left). Error: {}",
                correlationId,
                batch.topicPartition,
                this.retries - batch.attempts(),
                formatErrMsg(response));
            if (transactionManager != null)
                transactionManager.removeInFlightBatch(batch);
            this.accumulator.splitAndReenqueue(batch);
            maybeRemoveAndDeallocateBatch(batch);
            this.sensors.recordBatchSplit();
        } else if (error != Errors.NONE) {
            if (canRetry(batch, response, now)) {
                log.warn(
                    "Got error produce response with correlation id {} on topic-partition {}, retrying ({} attempts left). Error: {}",
                    correlationId,
                    batch.topicPartition,
                    this.retries - batch.attempts() - 1,
                    formatErrMsg(response));
                reenqueueBatch(batch, now);
            } else if (error == Errors.DUPLICATE_SEQUENCE_NUMBER) {
                // If we have received a duplicate sequence error, it means that the sequence number has advanced beyond
                // the sequence of the current batch, and we haven't retained batch metadata on the broker to return
                // the correct offset and timestamp.
                //
                // The only thing we can do is to return success to the user and not return a valid offset and timestamp.
                completeBatch(batch, response);
            } else {
                // tell the user the result of their request. We only adjust sequence numbers if the batch didn't exhaust
                // its retries -- if it did, we don't know whether the sequence number was accepted or not, and
                // thus it is not safe to reassign the sequence.
                failBatch(batch, response, batch.attempts() < this.retries);
            }
            if (error.exception() instanceof InvalidMetadataException) {
                if (error.exception() instanceof UnknownTopicOrPartitionException) {
                    log.warn("Received unknown topic or partition error in produce request on partition {}. The " +
                            "topic-partition may not exist or the user may not have Describe access to it",
                        batch.topicPartition);
                } else {
                    log.warn("Received invalid metadata error in produce request on partition {} due to {}. Going " +
                            "to request metadata update now", batch.topicPartition,
                            error.exception(response.errorMessage).toString());
                }
                metadata.requestUpdate();
            }
        } else {
            completeBatch(batch, response);
        }

        // Unmute the completed partition.
        if (guaranteeMessageOrder)
            this.accumulator.unmutePartition(batch.topicPartition);
    }

    /**
     * Format the error from a {@link ProduceResponse.PartitionResponse} in a user-friendly string
     * e.g "NETWORK_EXCEPTION. Error Message: Disconnected from node 0"
     */
    private String formatErrMsg(ProduceResponse.PartitionResponse response) {
        String errorMessageSuffix = (response.errorMessage == null || response.errorMessage.isEmpty()) ?
                "" : String.format(". Error Message: %s", response.errorMessage);
        return String.format("%s%s", response.error, errorMessageSuffix);
    }

    private void reenqueueBatch(ProducerBatch batch, long currentTimeMs) {
        this.accumulator.reenqueue(batch, currentTimeMs);
        maybeRemoveFromInflightBatches(batch);
        this.sensors.recordRetries(batch.topicPartition.topic(), batch.recordCount);
    }

    private void completeBatch(ProducerBatch batch, ProduceResponse.PartitionResponse response) {
        if (transactionManager != null) {
            transactionManager.handleCompletedBatch(batch, response);
        }

        if (batch.complete(response.baseOffset, response.logAppendTime)) {
            maybeRemoveAndDeallocateBatch(batch);
        }
    }

    private void failBatch(ProducerBatch batch,
                           ProduceResponse.PartitionResponse response,
                           boolean adjustSequenceNumbers) {
        final RuntimeException topLevelException;
        if (response.error == Errors.TOPIC_AUTHORIZATION_FAILED)
            topLevelException = new TopicAuthorizationException(Collections.singleton(batch.topicPartition.topic()));
        else if (response.error == Errors.CLUSTER_AUTHORIZATION_FAILED)
            topLevelException = new ClusterAuthorizationException("The producer is not authorized to do idempotent sends");
        else
            topLevelException = response.error.exception(response.errorMessage);

        if (response.recordErrors == null || response.recordErrors.isEmpty()) {
            failBatch(batch, topLevelException, adjustSequenceNumbers);
        } else {
            Map<Integer, RuntimeException> recordErrorMap = new HashMap<>(response.recordErrors.size());
            for (ProduceResponse.RecordError recordError : response.recordErrors) {
                // The API leaves us with some awkwardness interpreting the errors in the response.
                // We cannot differentiate between different error cases (such as INVALID_TIMESTAMP)
                // from the single error code at the partition level, so instead we use INVALID_RECORD
                // for all failed records and rely on the message to distinguish the cases.
                final String errorMessage;
                if (recordError.message != null) {
                    errorMessage = recordError.message;
                } else if (response.errorMessage != null) {
                    errorMessage = response.errorMessage;
                } else {
                    errorMessage = response.error.message();
                }

                // If the batch contained only a single record error, then we can unambiguously
                // use the exception type corresponding to the partition-level error code.
                if (response.recordErrors.size() == 1) {
                    recordErrorMap.put(recordError.batchIndex, response.error.exception(errorMessage));
                } else {
                    recordErrorMap.put(recordError.batchIndex, new InvalidRecordException(errorMessage));
                }
            }

            Function<Integer, RuntimeException> recordExceptions = batchIndex -> {
                RuntimeException exception = recordErrorMap.get(batchIndex);
                if (exception != null) {
                    return exception;
                } else {
                    // If the response contains record errors, then the records which failed validation
                    // will be present in the response. To avoid confusion for the remaining records, we
                    // return a generic exception.
                    return new KafkaException("Failed to append record because it was part of a batch " +
                        "which had one more more invalid records");
                }
            };

            failBatch(batch, topLevelException, recordExceptions, adjustSequenceNumbers);
        }
    }

    private void failBatch(
        ProducerBatch batch,
        RuntimeException topLevelException,
        boolean adjustSequenceNumbers
    ) {
        failBatch(batch, topLevelException, batchIndex -> topLevelException, adjustSequenceNumbers);
    }

    private void failBatch(
        ProducerBatch batch,
        RuntimeException topLevelException,
        Function<Integer, RuntimeException> recordExceptions,
        boolean adjustSequenceNumbers
    ) {
        if (transactionManager != null) {
            transactionManager.handleFailedBatch(batch, topLevelException, adjustSequenceNumbers);
        }

        this.sensors.recordErrors(batch.topicPartition.topic(), batch.recordCount);

        if (batch.completeExceptionally(topLevelException, recordExceptions)) {
            maybeRemoveAndDeallocateBatch(batch);
        }
    }

    /**
     * We can retry a send if the error is transient and the number of attempts taken is fewer than the maximum allowed.
     * We can also retry OutOfOrderSequence exceptions for future batches, since if the first batch has failed, the
     * future batches are certain to fail with an OutOfOrderSequence exception.
     */
    private boolean canRetry(ProducerBatch batch, ProduceResponse.PartitionResponse response, long now) {
        return !batch.hasReachedDeliveryTimeout(accumulator.getDeliveryTimeoutMs(), now) &&
            batch.attempts() < this.retries &&
            !batch.isDone() &&
            (transactionManager == null ?
                    response.error.exception() instanceof RetriableException :
                    transactionManager.canRetry(response, batch));
    }

    /**
     * Transfer the record batches into a list of produce requests on a per-node basis
     */
    private void sendProduceRequests(Map<Integer, List<ProducerBatch>> collated, long now) {
        for (Map.Entry<Integer, List<ProducerBatch>> entry : collated.entrySet())
            sendProduceRequest(now, entry.getKey(), acks, requestTimeoutMs, entry.getValue());
    }

    /**
     * Create a produce request from the given record batches
     */
    private void sendProduceRequest(long now, int destination, short acks, int timeout, List<ProducerBatch> batches) {
        if (batches.isEmpty())
            return;

        final Map<TopicPartition, ProducerBatch> recordsByPartition = new HashMap<>(batches.size());

        // find the minimum magic version used when creating the record sets
        byte minUsedMagic = apiVersions.maxUsableProduceMagic();
        for (ProducerBatch batch : batches) {
            if (batch.magic() < minUsedMagic)
                minUsedMagic = batch.magic();
        }
        ProduceRequestData.TopicProduceDataCollection tpd = new ProduceRequestData.TopicProduceDataCollection();
        for (ProducerBatch batch : batches) {
            TopicPartition tp = batch.topicPartition;
            MemoryRecords records = batch.records();

            // down convert if necessary to the minimum magic used. In general, there can be a delay between the time
            // that the producer starts building the batch and the time that we send the request, and we may have
            // chosen the message format based on out-dated metadata. In the worst case, we optimistically chose to use
            // the new message format, but found that the broker didn't support it, so we need to down-convert on the
            // client before sending. This is intended to handle edge cases around cluster upgrades where brokers may
            // not all support the same message format version. For example, if a partition migrates from a broker
            // which is supporting the new magic version to one which doesn't, then we will need to convert.
            if (!records.hasMatchingMagic(minUsedMagic))
                records = batch.records().downConvert(minUsedMagic, 0, time).records();
            ProduceRequestData.TopicProduceData tpData = tpd.find(tp.topic());
            if (tpData == null) {
                tpData = new ProduceRequestData.TopicProduceData().setName(tp.topic());
                tpd.add(tpData);
            }
            tpData.partitionData().add(new ProduceRequestData.PartitionProduceData()
                    .setIndex(tp.partition())
                    .setRecords(records));
            recordsByPartition.put(tp, batch);
        }

        String transactionalId = null;
        if (transactionManager != null && transactionManager.isTransactional()) {
            transactionalId = transactionManager.transactionalId();
        }

        ProduceRequest.Builder requestBuilder = ProduceRequest.forMagic(minUsedMagic,
                new ProduceRequestData()
                        .setAcks(acks)
                        .setTimeoutMs(timeout)
                        .setTransactionalId(transactionalId)
                        .setTopicData(tpd));
        RequestCompletionHandler callback = response -> handleProduceResponse(response, recordsByPartition, time.milliseconds());

        String nodeId = Integer.toString(destination);
        ClientRequest clientRequest = client.newClientRequest(nodeId, requestBuilder, now, acks != 0,
                requestTimeoutMs, callback);
        client.send(clientRequest, now);
        log.trace("Sent produce request to {}: {}", nodeId, requestBuilder);
    }

    /**
     * Wake up the selector associated with this send thread
     */
    public void wakeup() {
        this.client.wakeup();
    }

    public static Sensor throttleTimeSensor(SenderMetricsRegistry metrics) {
        Sensor produceThrottleTimeSensor = metrics.sensor("produce-throttle-time");
        produceThrottleTimeSensor.add(metrics.produceThrottleTimeAvg, new Avg());
        produceThrottleTimeSensor.add(metrics.produceThrottleTimeMax, new Max());
        return produceThrottleTimeSensor;
    }

    /**
     * A collection of sensors for the sender
     */
    private static class SenderMetrics {
        public final Sensor retrySensor;
        public final Sensor errorSensor;
        public final Sensor queueTimeSensor;
        public final Sensor requestTimeSensor;
        public final Sensor recordsPerRequestSensor;
        public final Sensor batchSizeSensor;
        public final Sensor compressionRateSensor;
        public final Sensor maxRecordSizeSensor;
        public final Sensor batchSplitSensor;
        private final SenderMetricsRegistry metrics;
        private final Time time;

        public SenderMetrics(SenderMetricsRegistry metrics, Metadata metadata, KafkaClient client, Time time) {
            this.metrics = metrics;
            this.time = time;

            this.batchSizeSensor = metrics.sensor("batch-size");
            this.batchSizeSensor.add(metrics.batchSizeAvg, new Avg());
            this.batchSizeSensor.add(metrics.batchSizeMax, new Max());

            this.compressionRateSensor = metrics.sensor("compression-rate");
            this.compressionRateSensor.add(metrics.compressionRateAvg, new Avg());

            this.queueTimeSensor = metrics.sensor("queue-time");
            this.queueTimeSensor.add(metrics.recordQueueTimeAvg, new Avg());
            this.queueTimeSensor.add(metrics.recordQueueTimeMax, new Max());

            this.requestTimeSensor = metrics.sensor("request-time");
            this.requestTimeSensor.add(metrics.requestLatencyAvg, new Avg());
            this.requestTimeSensor.add(metrics.requestLatencyMax, new Max());

            this.recordsPerRequestSensor = metrics.sensor("records-per-request");
            this.recordsPerRequestSensor.add(new Meter(metrics.recordSendRate, metrics.recordSendTotal));
            this.recordsPerRequestSensor.add(metrics.recordsPerRequestAvg, new Avg());

            this.retrySensor = metrics.sensor("record-retries");
            this.retrySensor.add(new Meter(metrics.recordRetryRate, metrics.recordRetryTotal));

            this.errorSensor = metrics.sensor("errors");
            this.errorSensor.add(new Meter(metrics.recordErrorRate, metrics.recordErrorTotal));

            this.maxRecordSizeSensor = metrics.sensor("record-size");
            this.maxRecordSizeSensor.add(metrics.recordSizeMax, new Max());
            this.maxRecordSizeSensor.add(metrics.recordSizeAvg, new Avg());

            this.metrics.addMetric(metrics.requestsInFlight, (config, now) -> client.inFlightRequestCount());
            this.metrics.addMetric(metrics.metadataAge,
                (config, now) -> (now - metadata.lastSuccessfulUpdate()) / 1000.0);

            this.batchSplitSensor = metrics.sensor("batch-split-rate");
            this.batchSplitSensor.add(new Meter(metrics.batchSplitRate, metrics.batchSplitTotal));
        }

        private void maybeRegisterTopicMetrics(String topic) {
            // if one sensor of the metrics has been registered for the topic,
            // then all other sensors should have been registered; and vice versa
            String topicRecordsCountName = "topic." + topic + ".records-per-batch";
            Sensor topicRecordCount = this.metrics.getSensor(topicRecordsCountName);
            if (topicRecordCount == null) {
                Map<String, String> metricTags = Collections.singletonMap("topic", topic);

                topicRecordCount = this.metrics.sensor(topicRecordsCountName);
                MetricName rateMetricName = this.metrics.topicRecordSendRate(metricTags);
                MetricName totalMetricName = this.metrics.topicRecordSendTotal(metricTags);
                topicRecordCount.add(new Meter(rateMetricName, totalMetricName));

                String topicByteRateName = "topic." + topic + ".bytes";
                Sensor topicByteRate = this.metrics.sensor(topicByteRateName);
                rateMetricName = this.metrics.topicByteRate(metricTags);
                totalMetricName = this.metrics.topicByteTotal(metricTags);
                topicByteRate.add(new Meter(rateMetricName, totalMetricName));

                String topicCompressionRateName = "topic." + topic + ".compression-rate";
                Sensor topicCompressionRate = this.metrics.sensor(topicCompressionRateName);
                MetricName m = this.metrics.topicCompressionRate(metricTags);
                topicCompressionRate.add(m, new Avg());

                String topicRetryName = "topic." + topic + ".record-retries";
                Sensor topicRetrySensor = this.metrics.sensor(topicRetryName);
                rateMetricName = this.metrics.topicRecordRetryRate(metricTags);
                totalMetricName = this.metrics.topicRecordRetryTotal(metricTags);
                topicRetrySensor.add(new Meter(rateMetricName, totalMetricName));

                String topicErrorName = "topic." + topic + ".record-errors";
                Sensor topicErrorSensor = this.metrics.sensor(topicErrorName);
                rateMetricName = this.metrics.topicRecordErrorRate(metricTags);
                totalMetricName = this.metrics.topicRecordErrorTotal(metricTags);
                topicErrorSensor.add(new Meter(rateMetricName, totalMetricName));
            }
        }

        public void updateProduceRequestMetrics(Map<Integer, List<ProducerBatch>> batches) {
            long now = time.milliseconds();
            for (List<ProducerBatch> nodeBatch : batches.values()) {
                int records = 0;
                for (ProducerBatch batch : nodeBatch) {
                    // register all per-topic metrics at once
                    String topic = batch.topicPartition.topic();
                    maybeRegisterTopicMetrics(topic);

                    // per-topic record send rate
                    String topicRecordsCountName = "topic." + topic + ".records-per-batch";
                    Sensor topicRecordCount = Objects.requireNonNull(this.metrics.getSensor(topicRecordsCountName));
                    topicRecordCount.record(batch.recordCount);

                    // per-topic bytes send rate
                    String topicByteRateName = "topic." + topic + ".bytes";
                    Sensor topicByteRate = Objects.requireNonNull(this.metrics.getSensor(topicByteRateName));
                    topicByteRate.record(batch.estimatedSizeInBytes());

                    // per-topic compression rate
                    String topicCompressionRateName = "topic." + topic + ".compression-rate";
                    Sensor topicCompressionRate = Objects.requireNonNull(this.metrics.getSensor(topicCompressionRateName));
                    topicCompressionRate.record(batch.compressionRatio());

                    // global metrics
                    this.batchSizeSensor.record(batch.estimatedSizeInBytes(), now);
                    this.queueTimeSensor.record(batch.queueTimeMs(), now);
                    this.compressionRateSensor.record(batch.compressionRatio());
                    this.maxRecordSizeSensor.record(batch.maxRecordSize, now);
                    records += batch.recordCount;
                }
                this.recordsPerRequestSensor.record(records, now);
            }
        }

        public void recordRetries(String topic, int count) {
            long now = time.milliseconds();
            this.retrySensor.record(count, now);
            String topicRetryName = "topic." + topic + ".record-retries";
            Sensor topicRetrySensor = this.metrics.getSensor(topicRetryName);
            if (topicRetrySensor != null)
                topicRetrySensor.record(count, now);
        }

        public void recordErrors(String topic, int count) {
            long now = time.milliseconds();
            this.errorSensor.record(count, now);
            String topicErrorName = "topic." + topic + ".record-errors";
            Sensor topicErrorSensor = this.metrics.getSensor(topicErrorName);
            if (topicErrorSensor != null)
                topicErrorSensor.record(count, now);
        }

        public void recordLatency(String node, long latency) {
            long now = time.milliseconds();
            this.requestTimeSensor.record(latency, now);
            if (!node.isEmpty()) {
                String nodeTimeName = "node-" + node + ".latency";
                Sensor nodeRequestTime = this.metrics.getSensor(nodeTimeName);
                if (nodeRequestTime != null)
                    nodeRequestTime.record(latency, now);
            }
        }

        void recordBatchSplit() {
            this.batchSplitSensor.record();
        }
    }

}
