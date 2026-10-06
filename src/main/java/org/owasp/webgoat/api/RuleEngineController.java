/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Risk-rule evaluation fixture: the expression supplied by the client is parsed and evaluated
 * directly with the default (unrestricted) SpEL evaluation context, so it is a SpEL injection /
 * remote code execution sink.
 */
@RestController
public class RuleEngineController {

  public record RuleRequest(String expression) {}

  private final ExpressionParser parser = new SpelExpressionParser();

  @PostMapping("/api/v1/rules/evaluate")
  public Object evaluate(@RequestBody RuleRequest request) {
    return parser.parseExpression(request.expression()).getValue();
  }
}
