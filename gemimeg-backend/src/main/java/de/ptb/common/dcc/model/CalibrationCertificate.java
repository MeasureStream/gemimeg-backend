package de.ptb.common.dcc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class CalibrationCertificate {

  @Id
  private String id;
  private String dccJson;
  private Date createdAt;
}
