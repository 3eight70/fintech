package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaTest;

public class OneProducerToOneConsumerKafkaTest extends BaseKafkaTest {
    public OneProducerToOneConsumerKafkaTest() {
        super(1, 1);
    }
}
