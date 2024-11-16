package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaTest;

public class OneProducerToThreeConsumerKafkaTest extends BaseKafkaTest {
    public OneProducerToThreeConsumerKafkaTest() {
        super(1, 3);
    }
}


