package ru.fintech.benchmark.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import ru.fintech.benchmark.BaseTest;
import ru.fintech.benchmark.utils.KafkaConsumerExample;
import ru.fintech.benchmark.utils.KafkaProducerExample;

public abstract class BaseKafkaTest extends BaseTest {
    private List<KafkaProducerExample> producers;
    private List<KafkaConsumerExample> consumers;

    private final int amountOfConsumers;
    private final int amountOfProducers;

    public BaseKafkaTest(Integer amountOfProducers, Integer amountOfConsumers) {
        this.amountOfConsumers = amountOfConsumers;
        this.amountOfProducers = amountOfProducers;
    }

    @Setup(Level.Trial)
    public void setup() throws ExecutionException, InterruptedException {
        producers = new ArrayList<>();
        for (int i = 0; i < amountOfProducers; i++) {
            producers.add(new KafkaProducerExample(i));
        }

        consumers = new ArrayList<>();
        for (int i = 0; i < amountOfConsumers; i++) {
            consumers.add(new KafkaConsumerExample());
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producers.forEach(KafkaProducerExample::close);
        consumers.forEach(KafkaConsumerExample::close);
    }

    @Benchmark
    public void kafkaProducersConsumers(Blackhole blackhole) {
        producers.forEach(producer -> {
            var sendFuture = producer.sendMessage(getMessage(messageSize, producer.getIndex()));
            blackhole.consume(sendFuture);
            blackhole.consume(producer);
        });
        consumers.forEach(consumer -> {
            var messages = consumer.consumeMessage();
            messages.forEach(blackhole::consume);
            blackhole.consume(consumer);
        });
        blackhole.consume(producers);
        blackhole.consume(consumers);
    }
}
