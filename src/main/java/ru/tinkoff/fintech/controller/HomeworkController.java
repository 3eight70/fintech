package ru.tinkoff.fintech.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.ArrayList;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeworkController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final Counter counter;

    public HomeworkController(MeterRegistry meterRegistry) {
        this.counter = meterRegistry.counter("homework.controller.request");
    }

    @GetMapping("/logs")
    public void createLogs() {
        try (var mdc = MDC.putCloseable("logId", UUID.randomUUID().toString())){
            log.info("Log info");
            log.warn("Log warn");
            log.error("Log error");
        }
    }

    @GetMapping("/counter")
    public void incrementMetric() {
        counter.increment();
    }

    @GetMapping("/stackoverflow")
    public void getStackOverflow() {
        getStackOverflow(1);
    }

    @GetMapping("/outofmemory")
    public void outOfMemory() {
        var list = new ArrayList<>();

        while (true) {
            list.add("OutOfMemory".repeat(100));
        }
    }

    private void getStackOverflow(int index) {
        getStackOverflow(index + 1);
    }
}
