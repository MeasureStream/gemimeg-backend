package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CalibrationCertificate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalibrationCertificateRepository extends MongoRepository<CalibrationCertificate, String> {
}
