package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema
@Data
public class ListDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private RichContentDto description;

  @Schema
  private LocalDateTime dateTime;

  @Schema
  private QuantityListDto quantities;

  @Schema
  private MethodListDto usedMethods;

  @Schema
  private List<ListDto> list;
}
