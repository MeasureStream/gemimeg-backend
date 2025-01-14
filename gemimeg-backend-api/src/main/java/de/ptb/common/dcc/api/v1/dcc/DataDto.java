package de.ptb.common.dcc.api.v1.dcc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class DataDto implements HasId, HasRefTypes, HasRefIds {

  @Schema
  protected String id;
  @Schema
  protected RichContentDto richContent;
  @Schema
  protected FormulaDto formula;
  @Schema
  protected ByteDataDto byteData;
  @Schema
  protected QuantityDto quantity;
  @Schema
  protected ListDto list;
  @Schema
  private List<String> refIds;
  @Schema
  private List<String> refTypes;
}
