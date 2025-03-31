/**
 * Copyright 2025 Physikalisch-Technische Bundesanstalt
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.ptb.common.dcc.data;

import de.ptb.common.dcc.config.CacheConfiguration;
import de.ptb.common.dcc.model.CacheItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CacheItemRepositoryTest {

  private final static String XML = """
      <?xml version="1.0" ?>
      <metadata>
        Für die Horde!
      </metadata>
      """;

  @Autowired
  private CacheItemRepository repository;

  @Autowired
  private CacheConfiguration configuration;

  private final long currentTimeMillis = System.currentTimeMillis();

  @BeforeEach
  void setUp() {
    long expiredTimeMillis = currentTimeMillis - configuration.getPersistLifespan().longValue() * 1001L;
    CacheItem expired = new CacheItem();
    expired.setMimeType("application/xml");
    expired.setFileName("FDH_expired.xml");
    expired.setFileContent(XML.getBytes(StandardCharsets.UTF_8));
    expired.setCreatedAt(new Date(expiredTimeMillis));
    CacheItem notExpired = new CacheItem();
    notExpired.setMimeType("application/xml");
    notExpired.setFileName("FDH_notExpired.xml");
    notExpired.setFileContent(XML.getBytes(StandardCharsets.UTF_8));
    notExpired.setCreatedAt(new Date(currentTimeMillis));
    repository.saveAll(List.of(expired, notExpired));
    assertEquals(2, repository.count());
  }

  @Test
  void findByCreatedAtLessThan_Ok() {
    List<CacheItem> actual = repository.findByCreatedAtLessThan(new Date(currentTimeMillis - 1000L));
    assertEquals(1, actual.size());
    assertEquals("FDH_expired.xml", actual.getFirst().getFileName());
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
    assertEquals(0, repository.count());
  }
}