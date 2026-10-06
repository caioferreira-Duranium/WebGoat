/*
 * SPDX-FileCopyrightText: Copyright © 2026 WebGoat authors
 * SPDX-License-Identifier: GPL-2.0-or-later
 */
package org.owasp.webgoat.api;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Document import/download fixture.
 *
 * <ul>
 *   <li>{@code /import} deserializes client-supplied XML with a default {@link XStream} instance
 *       (no type-permission allow-list configured), a classic insecure-deserialization /
 *       remote-code-execution sink.
 *   <li>{@code /download} concatenates the client-supplied file name onto a base directory with
 *       no sanitization, a classic path-traversal sink.
 * </ul>
 */
@RestController
public class DocumentController {

  @Value("${webgoat.server.directory}")
  private String serverDirectory;

  @PostMapping("/api/v1/documents/import")
  public Object importDocument(@RequestBody String xmlPayload) {
    XStream xStream = new XStream();
    return xStream.fromXML(xmlPayload);
  }

  @GetMapping("/api/v1/documents/download")
  public String download(@RequestParam String file) throws IOException {
    Path path = Path.of(serverDirectory, "documents", file);
    return Files.readString(path);
  }
}
