package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class AddSignatureDto {

  @Schema
  private CalibrationCertificateDto originalDto;

  @Schema
  private SignatureDto signatureDto;
}
