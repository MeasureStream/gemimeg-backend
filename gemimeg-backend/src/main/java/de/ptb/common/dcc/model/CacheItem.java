package de.ptb.common.dcc.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CacheItem {

  @Id
  private String id;
  private String callbackUrl;
  private String fileName;
  private String mimeType;
  private byte[] fileContent;
}
