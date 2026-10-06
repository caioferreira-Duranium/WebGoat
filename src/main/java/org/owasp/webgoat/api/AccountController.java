/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.owasp.webgoat.api.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Account API used as a BOLA/IDOR and mass-assignment fixture: any authenticated user can read or
 * overwrite any account regardless of {@code ownerUsername}, and the update endpoint persists
 * every field the client sends, including balance and admin_override.
 */
@RestController
@AllArgsConstructor
public class AccountController {

  private static final RowMapper<Account> ACCOUNT_ROW_MAPPER =
      (rs, rowNum) ->
          new Account(
              rs.getLong("id"),
              rs.getString("owner_username"),
              rs.getLong("customer_id"),
              rs.getString("account_type"),
              rs.getBigDecimal("balance"),
              rs.getBoolean("admin_override"));

  private final JdbcTemplate jdbcTemplate;

  @GetMapping("/api/v1/accounts/{accountId}")
  public Account getAccount(@PathVariable long accountId) {
    return jdbcTemplate.queryForObject(
        "SELECT * FROM CONTAINER.api_account WHERE id = ?", ACCOUNT_ROW_MAPPER, accountId);
  }

  @GetMapping("/api/v1/accounts/{accountId}/statement")
  public List<Map<String, Object>> getStatement(@PathVariable long accountId) {
    Account account =
        jdbcTemplate.queryForObject(
            "SELECT * FROM CONTAINER.api_account WHERE id = ?", ACCOUNT_ROW_MAPPER, accountId);
    return List.of(
        Map.of("description", "Saldo atual", "amount", account.getBalance()),
        Map.of("description", "Ultima movimentacao sintetica", "amount", new BigDecimal("-42.00")));
  }

  @PutMapping("/api/v1/accounts/{accountId}")
  public Account updateAccount(@PathVariable long accountId, @RequestBody Account account) {
    jdbcTemplate.update(
        "UPDATE CONTAINER.api_account SET owner_username = ?, customer_id = ?, account_type = ?,"
            + " balance = ?, admin_override = ? WHERE id = ?",
        account.getOwnerUsername(),
        account.getCustomerId(),
        account.getAccountType(),
        account.getBalance(),
        account.isAdminOverride(),
        accountId);
    account.setId(accountId);
    return account;
  }
}
