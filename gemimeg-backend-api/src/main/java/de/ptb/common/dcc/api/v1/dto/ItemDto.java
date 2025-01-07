package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class ItemDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  protected String id;

  @Schema
  protected List<String> refIds;

  @Schema
  protected List<String> refTypes;

  @Schema
  protected LanguageSpecificStringsDto name;

  @Schema
  protected String model;

  @Schema
  protected RichContentDto description;

  @Schema
  protected SoftwareListDto installedSoftwares;

  @Schema
  protected ContactDto manufacturer;

  @Schema
  private String classId;

  @Schema
  private String classReference;

  @Schema
  private IdentificationListDto identifications;
}
