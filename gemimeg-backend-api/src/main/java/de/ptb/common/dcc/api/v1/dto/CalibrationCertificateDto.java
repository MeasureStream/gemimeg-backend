package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static de.ptb.common.dcc.api.v1.CalibrationCertificateConstants.DEFAULT_SCHEMA_VERSION;

@Data
@Schema
@NoArgsConstructor
public class CalibrationCertificateDto implements HasId {

  @Schema
  private String id;

  @Schema
  private AdministrativeDataDto administrativeData;

  @Schema
  private MeasurementResultListDto measurementResults;

  @Schema
  private List<String> comments;

  @Schema
  private ByteDataDto document;

  @Schema
  private SignatureListDto signatures;

  @Schema
  private String schemaVersion = DEFAULT_SCHEMA_VERSION;
}
