package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CalibrationCertificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CalibrationCertificateRepository extends MongoRepository<CalibrationCertificate, String> {

  List<CalibrationCertificate> findByCreatedAtLessThan(Date createdAt);
}
