package ru.fintech.benchmark.rabbit.benchmarks;

import ru.fintech.benchmark.rabbit.BaseRabbitAsyncTest;

public class ThreeProducerToOneConsumerRabbitTest extends BaseRabbitAsyncTest {

    public ThreeProducerToOneConsumerRabbitTest() {
        super(3, 1);
    }
}
