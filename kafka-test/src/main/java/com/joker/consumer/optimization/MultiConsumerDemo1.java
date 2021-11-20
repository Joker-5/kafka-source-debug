package com.joker.consumer.optimization;

import com.joker.consumer.config.ConsumerDefaultConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 多线程消费消息，提升消费速度
 */
public class MultiConsumerDemo1 {
    public static final int consumerThreadNum = 8;
    public static final String topic = "joker-demo-topic1";

    public static void main(String[] args) {
        Properties props = ConsumerDefaultConfig.initConfig();
        for (int i = 0; i < consumerThreadNum; i++) {
            // 每个线程对应一个KafkaConsumer实例
            new KafkaConsumerThread(props, topic).start();
        }
    }

    public static class KafkaConsumerThread extends Thread {
        private KafkaConsumer<String, String> kafkaConsumer;

        public KafkaConsumerThread(Properties props, String topic) {
            this.kafkaConsumer = new KafkaConsumer<String, String>(props);
            this.kafkaConsumer.subscribe(Arrays.asList(topic));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        // process msg
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                kafkaConsumer.close();
            }
        }
    }
}
