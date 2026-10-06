/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import lombok.AllArgsConstructor;
import org.owasp.webgoat.api.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer profile fixture: returns the full persisted entity (internal notes, CPF, partner
 * token) with no response projection/DTO filtering, and echoes a hardcoded partner credential.
 */
@RestController
@AllArgsConstructor
public class CustomerProfileController {

  // Hardcoded credential used to call the (mocked) credit-bureau partner API.
  private static final String PARTNER_API_KEY = "partner-sk-test-7f2c1a9d4e6b8891fake";

  private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER =
      (rs, rowNum) ->
          new Customer(
              rs.getLong("id"),
              rs.getString("full_name"),
              rs.getString("cpf"),
              rs.getString("email"),
              rs.getString("phone"),
              rs.getString("internal_notes"),
              rs.getString("partner_token"));

  private final JdbcTemplate jdbcTemplate;

  @GetMapping("/api/v1/customers/{customerId}/profile")
  public ResponseEntity<Customer> getProfile(@PathVariable long customerId) {
    Customer customer =
        jdbcTemplate.queryForObject(
            "SELECT * FROM CONTAINER.api_customer WHERE id = ?", CUSTOMER_ROW_MAPPER, customerId);
    return ResponseEntity.ok().header("X-Partner-Api-Key", PARTNER_API_KEY).body(customer);
  }
}
