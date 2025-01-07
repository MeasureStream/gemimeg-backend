package de.ptb.common.dcc.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class MeasurementResultDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private SoftwareListDto usedSoftware;

  @Schema
  private MethodListDto usedMethods;

  @Schema
  private RichContentDto description;

  @Schema
  private ResultListDto results;

  @Schema
  private ConditionListDto influenceConditions;

  @Schema
  private EquipmentListDto equipment;

  @Schema
  private StatementListDto statements;
}
