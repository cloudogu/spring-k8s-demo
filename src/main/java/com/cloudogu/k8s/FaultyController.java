package com.cloudogu.k8s;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
public class FaultyController {

    @GetMapping("/faulty/random")
    public String faultyRandom() {
        int random = ThreadLocalRandom.current().nextInt();
        if (random < 0) {
            throw new IllegalStateException("only positive values are allowed");
        }
        return "" + random;
    }

}
