package com.joker.consumer.optimization;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.joker.consumer.config.ConsumerDefaultConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;

/**
 * 一般而言用{@link KafkaConsumer#poll(Duration)}拉取消息的速度是很快的
 * <p>
 * 消费整体性能瓶颈一般是在处理消息这一块
 * </p>
 * <p>
 * 因此可将处理消息模块改为多线程实现
 * </p>
 */
public class MultiConsumerDemo2 {
    public static final int consumerThreadNum = 8;
    public static final String topic = "joker-demo-topic1";
    public static final String groupId = "joker.demo";

    public static void main(String[] args) {
        Properties props = ConsumerDefaultConfig.initConfig();
        KafkaConsumerThread kafkaConsumerThread = new KafkaConsumerThread(props, topic, Runtime.getRuntime().availableProcessors());
        kafkaConsumerThread.start();
        for (int i = 0; i < consumerThreadNum; i++) {
            // 可在其中开启多个消费者线程
        }
    }

    public static class KafkaConsumerThread extends Thread {
        private KafkaConsumer<String, String> kafkaConsumer;
        private ExecutorService executorService;
        private int threadNumber;

        public KafkaConsumerThread(Properties props, String topic, int threadNumber) {
            this.kafkaConsumer = new KafkaConsumer<String, String>(props);
            this.kafkaConsumer.subscribe(Arrays.asList(topic));
            this.threadNumber = threadNumber;
            this.executorService = new ThreadPoolExecutor(
                    threadNumber,
                    threadNumber,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1000),
                    new ThreadPoolExecutor.CallerRunsPolicy());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                    if (!records.isEmpty()) {
                        // 多线程，每个线程处理一批消息
                        executorService.submit(new RecordHandler(records));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                kafkaConsumer.close();
            }
        }
    }

    // 用于处理消息
    public static class RecordHandler extends Thread {
        public final ConsumerRecords<String, String> records;

        public RecordHandler(ConsumerRecords<String, String> records) {
            this.records = records;
        }

        @Override
        public void run() {
            // process records...
        }
    }
}
