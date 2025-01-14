package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class XmlValuesDto {

  @Schema
  private List<DimensionDto> values;

  @Schema
  private UncertaintyListDto uncertainties;

  @Schema
  private CoverageIntervalListDto coverageIntervals;
}
