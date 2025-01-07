package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class IdentificationDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  protected String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  protected LanguageSpecificStringsDto name;

  @Schema
  protected String issuer;

  @Schema
  protected String value;
}
