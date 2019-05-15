package com.cloudogu.k8s;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class FaultyController {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/faulty/random")
    public String faultyRandom() {
        int random = ThreadLocalRandom.current().nextInt();
        if (random < 0) {
            throw new IllegalStateException("only positive values are allowed");
        }
        return "" + random;
    }

    @GetMapping("/faulty/counter")
    public long counter() {
        long count = counter.incrementAndGet();
        if (count % 2 == 0) {
            throw new IllegalStateException("only odd values or allowed");
        }
        return count;
    }

    @GetMapping("/faulty/fail")
    public String fail() {
        throw new IllegalStateException("this method will always fail");
    }


    @GetMapping("/faulty/slow")
    public String slow() throws InterruptedException {
        Thread.sleep(TimeUnit.SECONDS.toMillis(10l));
        int random = ThreadLocalRandom.current().nextInt();
        return "" + random;
    }

    @GetMapping("/faulty/counter-slow")
    public String partialSlow() throws InterruptedException {
        long count = counter.incrementAndGet();
        if (count % 2 == 0) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10l));
        }
        return "" + count;
    }

}
