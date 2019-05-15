package com.cloudogu.k8s;

import io.micrometer.core.annotation.Timed;
import io.opentracing.Tracer;
import io.opentracing.contrib.concurrent.TracedExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class RemoteController {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteController.class);

    private final ExecutorService executorService;
    private final RestTemplate restTemplate;
    private final List<String> remoteServices;

    @Autowired
    public RemoteController(Tracer tracer, RestTemplate restTemplate, @Value("${remote.services}") String remoteServicesAsString) {
        this.restTemplate = restTemplate;
        this.remoteServices = parseRemoteServices(remoteServicesAsString);
        this.executorService = new TracedExecutorService(Executors.newFixedThreadPool(4), tracer);
    }

    private List<String> parseRemoteServices(String remoteServicesAsString) {
        List<String> services = new ArrayList<>();
        for (String remoteService : remoteServicesAsString.split(",") ) {
            services.add(remoteService.trim());
        }
        return Collections.unmodifiableList(services);
    }

    @Timed("remote_add_sequential")
    @GetMapping("/remote/add-sequential")
    public String remoteAddSequential(@RequestParam(name = "iterations", defaultValue = "1", required = false) int iterations) {
        LOG.info("called remote add in sequential, with {} iterations", iterations);
        int result = 0;
        for (int i=0; i<iterations; i++) {
            result = calculate(result);
        }
        return "" + result;
    }

    @Timed("remote_add_parallel")
    @GetMapping("/remote/add-parallel")
    public String remoteAddParallel(@RequestParam(name = "iterations", defaultValue = "1", required = false) int iterations) throws ExecutionException, InterruptedException {
        LOG.info("called remote add in parallel, with {} iterations", iterations);

        List<Future<Integer>> numbers = new ArrayList<>();
        for (int i=0; i<iterations; i++) {
            for ( String remoteService : remoteServices ) {
                numbers.add( executorService.submit(queryRemoteServiceCallable(remoteService)) );
            }
        }

        int result = 0;
        for ( Future<Integer> future : numbers ) {
            result += future.get();
        }

        return "" + result;
    }

    @Timed("remote_counter")
    @GetMapping("/remote/counter/{svc}")
    public String remoteCount(@PathVariable("svc") String svc) {
        LOG.info("remote count {svc}");

        if (!remoteServices.contains(svc)) {
            throw new IllegalArgumentException("unknown service " + svc);
        }

        return "" + queryRemoteService(svc, "/faulty/counter");
    }

    @Timed("remote_random")
    @GetMapping("/remote/random/{svc}")
    public String remoteRandom(@PathVariable("svc") String svc) {
        LOG.info("remote count {svc}");

        if (!remoteServices.contains(svc)) {
            throw new IllegalArgumentException("unknown service " + svc);
        }

        return "" + queryRemoteService(svc, "/faulty/random");
    }

    @Timed("remote_fail")
    @GetMapping("/remote/fail/{svc}")
    public String remoteFail(@PathVariable("svc") String svc) {
        LOG.info("remote fail {svc}");

        if (!remoteServices.contains(svc)) {
            throw new IllegalArgumentException("unknown service " + svc);
        }

        return "" + queryRemoteService(svc, "/faulty/fail");
    }

    @Timed("remote_slow")
    @GetMapping("/remote/slow/{svc}")
    public String remoteSlow(@PathVariable("svc") String svc) {
        LOG.info("remote slow {svc}");

        if (!remoteServices.contains(svc)) {
            throw new IllegalArgumentException("unknown service " + svc);
        }

        return "" + queryRemoteService(svc, "/faulty/slow");
    }


    @Timed("remote_counter_slow")
    @GetMapping("/remote/counter-slow/{svc}")
    public String remoteCounterSlow(@PathVariable("svc") String svc) {
        LOG.info("remote counter-slow {svc}");

        if (!remoteServices.contains(svc)) {
            throw new IllegalArgumentException("unknown service " + svc);
        }

        return "" + queryRemoteService(svc, "/faulty/counter-slow");
    }

    private Callable<Integer> queryRemoteServiceCallable(String serviceName) {
        return () -> queryRemoteService(serviceName, "/math/random");
    }

    private int calculate(int value) {
        int result = value;
        for (String remoteService : remoteServices) {
            result += queryRemoteService(remoteService, "/math/random");
        }
        return result;
    }

    private Integer queryRemoteService(String serviceName, String suffix) {
        String url = createURL(serviceName, suffix);
        LOG.info("query remote url {}", url);
        return restTemplate.getForObject(url, Integer.class);
    }

    private String createURL(String serviceName, String suffix) {
        return "http://" + serviceName + suffix;
    }
}
