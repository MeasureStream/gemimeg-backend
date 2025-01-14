package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@Schema
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemListDto extends ArrayList<ItemDto> {

  @Schema
  protected LanguageSpecificStringsDto name;

  @Schema
  @SuppressWarnings("unused")
  protected String classId;

  @Schema
  protected RichContentDto description;

  @Schema
  @SuppressWarnings("unused")
  protected ContactDto owner;

  @Schema
  private String classReference;

  @Schema
  private IdentificationListDto identifications;
}
