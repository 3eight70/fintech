package ru.fintech.benchmark.rabbit.benchmarks;

import ru.fintech.benchmark.rabbit.BaseRabbitAsyncTest;

public class TenProducerToTenConsumerRabbitTest extends BaseRabbitAsyncTest {

    public TenProducerToTenConsumerRabbitTest() {
        super(10, 10);
    }
}
