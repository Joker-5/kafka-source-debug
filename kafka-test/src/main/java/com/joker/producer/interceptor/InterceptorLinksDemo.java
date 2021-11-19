package com.joker.producer.interceptor;

import com.joker.producer.config.DefaultConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.*;

/**
 * 生产者拦截器调用链Demo
 */
public class InterceptorLinksDemo implements ProducerInterceptor<String, String> {
    private volatile long sendSuccess = 0;
    private volatile long sendFailure = 0;

    // 给每条msg都加一个「prefix2-」前缀
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String modifiedValue = "prefix2-" + record.value();
        return new ProducerRecord<String, String>(record.topic(),
                record.partition(), record.timestamp(),
                record.key(), modifiedValue, record.headers());
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            sendSuccess++;
        } else {
            sendFailure++;
        }
    }

    @Override
    public void close() {
        double successRatio = (double) sendSuccess / (sendSuccess + sendFailure);
        System.out.println("[INFO] successRatio=" + String.format("%f", successRatio * 100) + "%");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }

    public static void main(String[] args) {
        List<String> classPathList = Arrays.asList(InterceptorDemo.class.getName(), InterceptorLinksDemo.class.getName());
        Properties props = DefaultConfig.initConfigWithCustomizedInterceptor(String.join(",", classPathList));

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>("topic-demo", "Joker");
        producer.send(record);
    }
}
