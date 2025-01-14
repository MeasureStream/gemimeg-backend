package de.ptb.common.dcc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CalibrationCertificate {

  @Id
  private String id;
  private String dccJson;
}
