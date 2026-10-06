/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer search fixture: the query is built by string concatenation instead of a
 * parameterized/prepared statement, so {@code name} is a classic SQL injection sink.
 */
@RestController
@AllArgsConstructor
public class CustomerSearchController {

  private final JdbcTemplate jdbcTemplate;

  @GetMapping("/api/v1/customers/search")
  public List<Map<String, Object>> search(@RequestParam String name) {
    String query = "SELECT id, full_name, email, phone FROM CONTAINER.api_customer WHERE full_name LIKE '%" + name + "%'";
    return jdbcTemplate.queryForList(query);
  }
}
