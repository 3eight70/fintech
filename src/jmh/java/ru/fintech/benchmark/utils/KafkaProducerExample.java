package ru.fintech.benchmark.utils;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import static ru.fintech.benchmark.utils.KafkaConsumerExample.TOPIC;

public class KafkaProducerExample {
    private final KafkaProducer<String, String> producer;
    private final int producerIndex;

    public KafkaProducerExample(int producerIndex) throws ExecutionException, InterruptedException {
        this.producerIndex = producerIndex;
        Properties adminProperties = new Properties();
        adminProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient adminClient = AdminClient.create(adminProperties);

        boolean topicExists = adminClient.listTopics(new ListTopicsOptions().listInternal(false))
                .names()
                .get()
                .contains(TOPIC);

        if (!topicExists) {
            NewTopic newTopic = new NewTopic(TOPIC, 5, (short) 1);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        }

        adminClient.close();

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");

        this.producer = new KafkaProducer<>(properties);
    }

    public int getIndex() {
        return producerIndex;
    }

    public Future<RecordMetadata> sendMessage(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "key", message);

        return producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                exception.printStackTrace();
            } else {
//                System.out.println("Отправлено сообщение: " + message);
            }
        });
    }

    public void close() {
        if (producer != null) {
            producer.close();
        }
    }
}