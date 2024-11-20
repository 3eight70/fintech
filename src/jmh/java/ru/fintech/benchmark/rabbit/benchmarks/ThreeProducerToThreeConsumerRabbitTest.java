package ru.fintech.benchmark.rabbit.benchmarks;

import ru.fintech.benchmark.rabbit.BaseRabbitAsyncTest;

public class ThreeProducerToThreeConsumerRabbitTest extends BaseRabbitAsyncTest {

    public ThreeProducerToThreeConsumerRabbitTest() {
        super(3, 3);
    }
}
