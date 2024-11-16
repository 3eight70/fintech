package ru.fintech.benchmark.rabbit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import ru.fintech.benchmark.BaseTest;
import ru.fintech.benchmark.utils.RabbitMqConsumer;
import ru.fintech.benchmark.utils.RabbitMqProducer;

public abstract class BaseRabbitAsyncTest extends BaseTest {
    private List<RabbitMqProducer> producers;
    private List<RabbitMqConsumer> consumers;

    private final int amountOfConsumers;
    private final int amountOfProducers;

    private ExecutorService producerExecutor;
    private ExecutorService consumerExecutor;

    public BaseRabbitAsyncTest(Integer amountOfProducers, Integer amountOfConsumers) {
        this.amountOfConsumers = amountOfConsumers;
        this.amountOfProducers = amountOfProducers;
    }

    @Setup(Level.Trial)
    public void setup() throws IOException, TimeoutException {
        this.producers = new ArrayList<>();
        this.consumers = new ArrayList<>();

        for (int i = 0; i < amountOfProducers; i++) {
            producers.add(new RabbitMqProducer(i));
        }

        for (int i = 0; i < amountOfConsumers; i++) {
            consumers.add(new RabbitMqConsumer());
        }

        producerExecutor = Executors.newFixedThreadPool(amountOfProducers);
        consumerExecutor = Executors.newFixedThreadPool(amountOfConsumers);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        consumers.forEach(RabbitMqConsumer::close);
        producers.forEach(RabbitMqProducer::close);
        producerExecutor.shutdown();
        consumerExecutor.shutdown();
    }

    @Benchmark
    public void rabbitProducerConsumer(Blackhole blackhole) throws ExecutionException, InterruptedException {
        List<Future<?>> producerFutures = new ArrayList<>();
        List<Future<?>> consumerFutures = new ArrayList<>();

        producers.forEach(producer -> producerFutures.add(
                        producerExecutor.submit(() -> {
                            try {
                                var message = "Тестовое сообщение " + producer.getIndex();
                                producer.sendMessage(message);
                                blackhole.consume(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            blackhole.consume(producer);
                        })
                )
        );
        consumers.forEach(consumer -> consumerFutures.add(
                        consumerExecutor.submit(() -> {
                            try {
                                var message = consumer.consumeMessage();
                                blackhole.consume(message);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            blackhole.consume(consumer);
                        })
                )
        );

        for (Future<?> future : producerFutures) {
            future.get();
            blackhole.consume(future);
        }
        for (Future<?> future : consumerFutures) {
            future.get();
            blackhole.consume(future);
        }

        blackhole.consume(producerFutures);
        blackhole.consume(consumerFutures);
    }
}
