package com.joker.producer.interceptor;

import com.joker.producer.config.DefaultConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;
import java.util.Properties;

/**
 * 自定义生产者拦截器
 */
public class InterceptorDemo implements ProducerInterceptor<String, String> {
    private volatile long sendSuccess = 0;
    private volatile long sendFailure = 0;

    // 给每条msg都加一个「prefix1-」前缀
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String modifiedValue = "prefix1-" + record.value();
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
        Properties props = DefaultConfig.initConfigWithCustomizedInterceptor(InterceptorDemo.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        ProducerRecord<String, String> record = new ProducerRecord<>("topic-demo", "Joker");
        producer.send(record);
    }
}
