package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema
public class DataListDto extends ArrayList<DataDto> implements HasId, HasRefIds, HasRefTypes {

  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;


}
