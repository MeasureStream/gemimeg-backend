package de.ptb.common.dcc.api.v1.cache;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class StoreResponseDto {

  @Schema
  private String retrievalUrl;
}
