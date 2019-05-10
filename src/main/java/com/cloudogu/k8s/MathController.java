package com.cloudogu.k8s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

@RestController
public class MathController {

    private static final Logger LOG = LoggerFactory.getLogger(MathController.class);

    @GetMapping("/math/add")
    public String mathAdd(@RequestParam(name = "a") int a, @RequestParam(name = "b") int b) {
        LOG.info("calculate {} + {}", a, b);
        return "" + (a + b);
    }

    @GetMapping("/math/random")
    public String mathRandom() {
        String random = "" + ThreadLocalRandom.current().nextInt();
        LOG.info("calculated random {}", random);
        return random;
    }

    @GetMapping("/math/sqrt")
    public String mathSqrt(@RequestParam(name = "iterations", required = false, defaultValue = "1000000") int iterations) {
        LOG.info("called sqrt with {} iterations", iterations);
        double x = 0.0001;
        for (int i = 0; i <= iterations; i++) {
            x += Math.sqrt(x);
        }
        return "" + x;
    }
}
