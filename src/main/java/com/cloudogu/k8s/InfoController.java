package com.cloudogu.k8s;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class InfoController {

    private static final Logger LOG = LoggerFactory.getLogger(InfoController.class);

    private final HostnameResolver hostnameResolver;
    private final String version;

    @Autowired
    public InfoController(HostnameResolver hostnameResolver, @Value("${info.build.version}") String version) {
        this.hostnameResolver = hostnameResolver;
        this.version = version;
    }

    @GetMapping("/info/hostname")
    public String hostname() throws IOException, InterruptedException {
        LOG.info("called info hostname");
        String hostname = hostnameResolver.resolve();
        LOG.info("resolve {} returned", hostname);
        return hostname;
    }

    @GetMapping("/info/version")
    public String version()  {
        LOG.info("called info version {}", version);
        return version;
    }

}
