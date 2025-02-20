package de.ptb.common.dcc.data;

import de.ptb.common.dcc.config.CalibrationCertificateConfiguration;
import de.ptb.common.dcc.model.CalibrationCertificate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CalibrationCertificateRepositoryTest {

  @Autowired
  private CalibrationCertificateRepository repository;

  @Autowired
  private CalibrationCertificateConfiguration configuration;

  private String expiredId;

  private final long currentTimeMillis = System.currentTimeMillis();

  @BeforeEach
  void setUp() {
    long expiredTimeMillis = currentTimeMillis - configuration.getPersistLifespan().longValue() * 1001L;
    CalibrationCertificate expired = new CalibrationCertificate();
    expiredId = "FDH_" + expiredTimeMillis;
    expired.setId(expiredId);
    expired.setDccJson("{}");
    expired.setCreatedAt(new Date(expiredTimeMillis));
    CalibrationCertificate notExpired = new CalibrationCertificate();
    notExpired.setId("FDH_" + currentTimeMillis);
    notExpired.setDccJson("{}");
    notExpired.setCreatedAt(new Date(currentTimeMillis));
    repository.saveAll(List.of(expired, notExpired));
    assertEquals(2, repository.count());
  }

  @Test
  void findByCreatedAtLessThan_Ok() {
    List<CalibrationCertificate> actual = repository.findByCreatedAtLessThan(new Date(currentTimeMillis - 1000L));
    assertEquals(1, actual.size());
    assertEquals(expiredId, actual.getFirst().getId());
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
    assertEquals(0, repository.count());
  }
}