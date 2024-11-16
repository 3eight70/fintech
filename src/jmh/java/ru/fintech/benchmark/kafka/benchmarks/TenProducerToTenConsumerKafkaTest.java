package ru.fintech.benchmark.kafka.benchmarks;

import ru.fintech.benchmark.kafka.BaseKafkaAsyncTest;

public class TenProducerToTenConsumerKafkaTest extends BaseKafkaAsyncTest {
    public TenProducerToTenConsumerKafkaTest() {
        super(10, 10);
    }
}

