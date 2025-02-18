/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Deprecated
@Data
@Schema
public class ExpandedUncDto {

  @Schema
  private double uncertainty;

  @Schema
  private double coverageFactor;

  @Schema
  private double coverageProbability;

  @Schema
  private String distribution;
}
