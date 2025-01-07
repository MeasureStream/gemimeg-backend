package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DimensionDto {

  @Schema
  private Number value;

  @Schema
  private String unit;
}
