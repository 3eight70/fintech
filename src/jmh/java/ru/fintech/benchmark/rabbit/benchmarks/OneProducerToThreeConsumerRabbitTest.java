package ru.fintech.benchmark.rabbit.benchmarks;

import ru.fintech.benchmark.rabbit.BaseRabbitAsyncTest;

public class OneProducerToThreeConsumerRabbitTest extends BaseRabbitAsyncTest {

    public OneProducerToThreeConsumerRabbitTest() {
        super(1, 3);
    }
}
