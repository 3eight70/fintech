package ru.fintech.benchmark.rabbit.benchmarks;

import ru.fintech.benchmark.rabbit.BaseRabbitTest;

public class OneProducerToOneConsumerRabbitTest extends BaseRabbitTest {

    public OneProducerToOneConsumerRabbitTest() {
        super(1, 1);
    }
}
