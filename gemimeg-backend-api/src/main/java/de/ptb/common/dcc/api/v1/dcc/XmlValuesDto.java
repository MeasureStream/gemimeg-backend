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
  private ExpandedUncListDto expandedUncList;

  @Schema
  private ExpandedMUListDto expandedMUList;

  @Schema
  private CoverageIntervalListDto coverageIntervals;
}
