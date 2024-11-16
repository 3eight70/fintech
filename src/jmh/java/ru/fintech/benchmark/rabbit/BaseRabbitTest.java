package ru.fintech.benchmark.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import ru.fintech.benchmark.BaseTest;
import ru.fintech.benchmark.utils.RabbitMqConsumer;
import ru.fintech.benchmark.utils.RabbitMqProducer;

public abstract class BaseRabbitTest extends BaseTest {
    private List<RabbitMqProducer> producers;
    private List<RabbitMqConsumer> consumers;

    private final int amountOfConsumers;
    private final int amountOfProducers;

    public BaseRabbitTest(Integer amountOfProducers, Integer amountOfConsumers) {
        this.amountOfConsumers = amountOfConsumers;
        this.amountOfProducers = amountOfProducers;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        producers = new ArrayList<>();
        consumers = new ArrayList<>();

        for (int i = 0; i < amountOfProducers; i++) {
            producers.add(new RabbitMqProducer(i));
        }

        for (int i = 0; i < amountOfConsumers; i++) {
            consumers.add(new RabbitMqConsumer());
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        consumers.forEach(RabbitMqConsumer::close);
        producers.forEach(RabbitMqProducer::close);
    }

    @Benchmark
    public void rabbitProducerConsumer(Blackhole blackhole) {
        producers.forEach(producer -> {
            try {
                var message = "Тестовое сообщение " + producer.getIndex();
                producer.sendMessage(message);
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(producer);
        });
        consumers.forEach(consumer -> {
            try {
                var message = consumer.consumeMessage();
                blackhole.consume(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            blackhole.consume(consumer);
        });
        blackhole.consume(producers);
        blackhole.consume(consumers);
    }
}
