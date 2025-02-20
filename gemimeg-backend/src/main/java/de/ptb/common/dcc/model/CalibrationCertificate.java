package de.ptb.common.dcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "DCC")
public class CalibrationCertificate {

  @Id
  @Column(name = "ID", nullable = false, unique = true)
  private String id;

  @Column(name = "DCC_JSON", nullable = false)
  private String dccJson;

  @Column(name = "CREATED_AT", nullable = false)
  private Date createdAt;
}
