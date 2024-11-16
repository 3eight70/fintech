package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaAsyncTest;

public class OneProducerToOneConsumerKafkaTest extends BaseKafkaAsyncTest {
    public OneProducerToOneConsumerKafkaTest() {
        super(1, 1);
    }
}
