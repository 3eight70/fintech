package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaTest;

public class ThreeProducerToThreeConsumerKafkaTest extends BaseKafkaTest {
    public ThreeProducerToThreeConsumerKafkaTest() {
        super(3, 3);
    }
}


