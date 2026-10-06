/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Network diagnostics fixture: the host supplied by the client is interpolated directly into a
 * shell command, so it is a classic OS command injection sink (e.g. {@code host=$(id)} or {@code
 * host=; id}).
 */
@RestController
public class DiagnosticsController {

  public record PingRequest(String host) {}

  @PostMapping("/api/v1/diagnostics/ping")
  public String ping(@RequestBody PingRequest request) throws IOException, InterruptedException {
    ProcessBuilder processBuilder =
        new ProcessBuilder("/bin/sh", "-c", "ping -c 2 " + request.host());
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    process.waitFor();
    return output;
  }
}
