package de.ptb.common.dcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "DCC")
public class CalibrationCertificate {

  @Id
  @Column(name = "ID", nullable = false, unique = true)
  private String id;

  @Lob
  @Column(name = "DCC_JSON", nullable = false)
  private String dccJson;
}
