package ru.fintech.benchmark.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;
import ru.fintech.benchmark.BaseTest;
import ru.fintech.benchmark.utils.KafkaConsumerExample;
import ru.fintech.benchmark.utils.KafkaProducerExample;

public abstract class BaseKafkaAsyncTest extends BaseTest {
    private List<KafkaProducerExample> producers;
    private List<KafkaConsumerExample> consumers;

    private ExecutorService producerExecutor;
    private ExecutorService consumerExecutor;

    private final int amountOfConsumers;
    private final int amountOfProducers;

    public BaseKafkaAsyncTest(Integer amountOfProducers, Integer amountOfConsumers) {
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

        producerExecutor = Executors.newFixedThreadPool(amountOfProducers);
        consumerExecutor = Executors.newFixedThreadPool(amountOfConsumers);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        producers.forEach(KafkaProducerExample::close);
        consumers.forEach(KafkaConsumerExample::close);
        producerExecutor.shutdown();
        consumerExecutor.shutdown();
    }

    @Benchmark
    public void kafkaProducersConsumers(Blackhole blackhole) throws ExecutionException, InterruptedException {
        List<Future<?>> producerFutures = new ArrayList<>();
        List<Future<?>> consumerFutures = new ArrayList<>();

        for (KafkaProducerExample producer : producers) {
            producerFutures.add(producerExecutor.submit(() -> {
                var sendFuture = producer.sendMessage("Тестовое сообщение " + producer.getIndex());
                blackhole.consume(sendFuture);
            }));
            blackhole.consume(producer);
        }

        for (KafkaConsumerExample consumer : consumers) {
            consumerFutures.add(consumerExecutor.submit(() -> {
                var message = consumer.consumeMessage();
                blackhole.consume(message);
            }));
            blackhole.consume(consumer);
        }

        for (Future<?> future : producerFutures) {
            future.get();
            blackhole.consume(future);
        }
        for (Future<?> future : consumerFutures) {
            future.get();
            blackhole.consume(future);
        }

        blackhole.consume(producers);
        blackhole.consume(consumers);
    }
}
