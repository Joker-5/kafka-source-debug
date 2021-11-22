package com.joker.consumer.partition;

import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.protocol.types.Field;

import java.util.*;

/**
 * 自定义分区器
 */
public class CustomPartitionDemo {
}

class RandomAssignor extends AbstractPartitionAssignor {

    @Override
    public String name() {
        return "random";
    }

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        Map<String, List<String>> consumersPerTopic = consumersPerTopic(subscriptions);
        Map<String, List<TopicPartition>> assignment = new HashMap<>();
        for (String memberId : subscriptions.keySet()) {
            assignment.put(memberId, new ArrayList<>());
        }

        // 对每个topic的分区进行分配
        for (Map.Entry<String, List<String>> topicEntry : consumersPerTopic.entrySet()) {
            String topic = topicEntry.getKey();
            List<String> consumerForTopic = topicEntry.getValue();
            int consumerSize = consumerForTopic.size();

            Integer numPartitionsForTopic = partitionsPerTopic.get(topic);
            if (numPartitionsForTopic == null) {
                continue;
            }

            // 当前topic下的partition
            List<TopicPartition> partitions = AbstractPartitionAssignor.partitions(topic, numPartitionsForTopic);
            // 将每个分区随机分配给consumer
            for (TopicPartition partition : partitions) {
                int rand = new Random().nextInt(consumerSize);
                String randomConsumer = consumerForTopic.get(rand);
                assignment.get(randomConsumer).add(partition);
            }
        }
        return assignment;
    }

    // 获取每个topic对应的consumer列表
    private Map<String, List<String>> consumersPerTopic(Map<String, Subscription> consumerMetaData) {
        Map<String, List<String>> res = new HashMap<>();
        for (Map.Entry<String, Subscription> subscriptionEntry : consumerMetaData.entrySet()) {
            String consumerId = subscriptionEntry.getKey();
            for (String topic : subscriptionEntry.getValue().topics()) {
                put(res, topic, consumerId);
            }
        }
        return res;
    }
}
