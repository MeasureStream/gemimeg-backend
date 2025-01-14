package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class CalibrationLaboratoryDto {

  @Schema
  private String calibrationLaboratoryCode;

  @Schema
  private ContactDto contact;
}
