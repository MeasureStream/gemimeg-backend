package de.ptb.common.dcc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document
public class CacheItem {

  @Id
  private String id;
  private String callbackUrl;
  private String fileName;
  private String mimeType;
  private byte[] fileContent;
  private Date createdAt;
}
