package de.ptb.common.dcc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class CalibrationCertificate {

  @Id
  private String id;
  private String dccJson;
  private Date createdAt;
}
