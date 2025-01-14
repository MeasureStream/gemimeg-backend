/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class ConditionDto implements HasId, HasRefIds, HasRefTypes {

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
  private String status;

  @Schema
  private String certificateValue;

  @Schema
  private String certificateId;

  @Schema
  private String referralId;

  @Schema
  private LanguageSpecificStringsDto certificateReferral;

  @Schema
  private String certificateProcedure;

  @Schema
  private DataListDto data;
}
