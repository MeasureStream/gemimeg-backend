/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class ResultDto implements HasId, HasRefIds, HasRefTypes {

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
  private DataListDto data;

}
