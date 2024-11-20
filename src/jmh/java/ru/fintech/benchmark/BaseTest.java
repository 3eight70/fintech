package ru.fintech.benchmark;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Fork(value = 1)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
public abstract class BaseTest {
    @Param({"small", "big"})
    protected String messageSize;

    protected String getMessage(String messageSize, int index) {
        var message = "Тестовое сообщение " + index;
        return messageSize.equals("small") ? message : message.repeat(10000);
    }
}
