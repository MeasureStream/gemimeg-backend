package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class ResponsiblePersonDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  protected String id;
  @Schema
  private List<String> refIds;
  @Schema
  private List<String> refTypes;
  @Schema
  private ContactDto contact;
  @Schema
  private String role;
  @Schema
  private Boolean mainSigner;
  @Schema
  private Boolean cryptElectronicSeal;
  @Schema
  private Boolean cryptElectronicSignature;
  @Schema
  private Boolean cryptElectronicTimeStamp;
}
