/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import java.net.URI;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Webhook validation fixture: fetches whatever URL the client supplies, server-side, with no
 * allow-list and no block on private/link-local ranges (e.g. cloud metadata at
 * 169.254.169.254) — a classic SSRF sink.
 */
@RestController
public class WebhookController {

  public record WebhookRequest(String callbackUrl) {}

  private final RestTemplate restTemplate = new RestTemplate();

  @PostMapping("/api/v1/webhooks/validate")
  public String validate(@RequestBody WebhookRequest request) {
    return restTemplate.getForObject(URI.create(request.callbackUrl()), String.class);
  }
}
