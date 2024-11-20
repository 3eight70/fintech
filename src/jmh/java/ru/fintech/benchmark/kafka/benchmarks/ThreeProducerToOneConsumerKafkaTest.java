package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaTest;

public class ThreeProducerToOneConsumerKafkaTest extends BaseKafkaTest {
    public ThreeProducerToOneConsumerKafkaTest() {
        super(3, 1);
    }
}

