package com.cloudogu.k8s;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public final class HostnameResolver {

    String resolve() throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec("hostname");
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException("resolve command exited with " + exitCode);
        }
        return StreamUtils.copyToString(process.getInputStream(), StandardCharsets.UTF_8);
    }
}
