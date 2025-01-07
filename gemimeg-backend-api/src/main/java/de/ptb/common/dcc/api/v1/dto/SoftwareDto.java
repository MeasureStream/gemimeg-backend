package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class SoftwareDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  List<String> refIds;

  @Schema
  List<String> refTypes;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private String version;

  @Schema
  private String type;

  @Schema
  private RichContentDto description;
}
