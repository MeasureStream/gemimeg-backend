package de.ptb.common.dcc.api.v1.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class RequestDto {

  @Schema
  private String callbackUrl;

  @Schema
  private String fileName;

  @Schema
  private String mimeType;

  @Schema
  private byte[] fileContent;
}
