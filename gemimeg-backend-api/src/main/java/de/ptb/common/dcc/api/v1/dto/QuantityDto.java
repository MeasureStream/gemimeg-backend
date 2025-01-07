package de.ptb.common.dcc.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema
public class QuantityDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private String label;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private DimensionDto dimension;

  @Schema
  private String quantityTypeName;

  @Schema
  private HybridValues hybridValues;

  @Schema
  private RichContentDto noQuantity;

  @Schema
  private RichContentDto description;

  @Schema
  private XmlValuesDto xmlValues;

  @Schema
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSS")
  private LocalDateTime timestamp;

  @Schema
  private MethodListDto usedMethods;

  @Schema
  private UncertaintyDto uncertainty;

  @Schema
  private CoverageIntervalDto coverageInterval;

  @Data
  @Schema
  public static class HybridValues {

    @Schema
    DimensionListDto dimensions;

    @Schema
    private List<String> quantitySubTypeNames;

    @Schema
    private List<String> labelList;

    @Schema
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private List<LocalDateTime> dateTimeList;

    @Schema
    private UncertaintyListDto uncertaintyList;

    @Schema
    private CoverageIntervalListDto coverageIntervalList;
  }
}
