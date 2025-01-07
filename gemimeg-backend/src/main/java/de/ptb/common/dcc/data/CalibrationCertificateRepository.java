package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CalibrationCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalibrationCertificateRepository extends JpaRepository<CalibrationCertificate, String> {
}
