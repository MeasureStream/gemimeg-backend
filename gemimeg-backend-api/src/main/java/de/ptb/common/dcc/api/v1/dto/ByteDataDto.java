package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class ByteDataDto implements HasId, HasRefIds {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private byte[] content;

  @Schema
  private String mimeType;

  @Schema
  private String fileName;

  @Schema
  private RichContentDto description;
}
