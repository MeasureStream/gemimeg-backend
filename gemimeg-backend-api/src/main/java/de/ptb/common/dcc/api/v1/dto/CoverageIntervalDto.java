/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class CoverageIntervalDto {

  @Schema
  private double standardUncertainty;

  @Schema
  private double intervalMinimum;

  @Schema
  private double intervalMaximum;

  @Schema
  private double coverageProbability;

  @Schema
  private String distribution;
}
