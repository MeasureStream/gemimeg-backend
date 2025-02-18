package de.ptb.common.dcc.data;

import de.ptb.common.dcc.config.CacheConfiguration;
import de.ptb.common.dcc.model.CacheItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
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

  @Autowired
  private MongoTemplate mongoTemplate;

  private final long currentTimeMillis = System.currentTimeMillis();

  @BeforeEach
  void setUp() {
    long expiredTimeMillis = currentTimeMillis - configuration.getPersistLifespan().longValue() * 1001L;
    CacheItem expired = new CacheItem();
    expired.setMimeType("application/xml");
    expired.setFileName("FDH_expired.xml");
    expired.setFileContent(XML.getBytes(StandardCharsets.UTF_8));
    expired.setCreatedAt(new Date(expiredTimeMillis));
    mongoTemplate.insert(expired);
    CacheItem notExpired = new CacheItem();
    notExpired.setMimeType("application/xml");
    notExpired.setFileName("FDH_notExpired.xml");
    notExpired.setFileContent(XML.getBytes(StandardCharsets.UTF_8));
    notExpired.setCreatedAt(new Date(currentTimeMillis));
    mongoTemplate.insert(notExpired);
  }

  @Test
  void findByCreatedAtLessThan_Ok() {
    List<CacheItem> actual = repository.findByCreatedAtLessThan(new Date(currentTimeMillis - 1000L));
    assertEquals(1, actual.size());
    assertEquals("FDH_expired.xml", actual.getFirst().getFileName());
  }

  @AfterEach
  void tearDown() {
    mongoTemplate.getDb().drop();
  }
}