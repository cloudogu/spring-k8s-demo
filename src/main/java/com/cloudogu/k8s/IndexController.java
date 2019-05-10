package com.cloudogu.k8s;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class IndexController {

    private String startedAt = LocalDateTime.now().toString();

    private final HostnameResolver hostnameResolver;
    private final String version;

    @Autowired
    public IndexController(HostnameResolver hostnameResolver, @Value("${info.build.version}") String version) {
        this.hostnameResolver = hostnameResolver;
        this.version = version;
    }

    @GetMapping("/")
    public String index(Model model) throws IOException, InterruptedException {
        String hostname = hostnameResolver.resolve();
        model.addAttribute("hostname", hostname);
        model.addAttribute("version", version);
        model.addAttribute("title", hostname + " (" + version + ")");
        model.addAttribute("startedAt", startedAt);
        return "index";
    }

}
