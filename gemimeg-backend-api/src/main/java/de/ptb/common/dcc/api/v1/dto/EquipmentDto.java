package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema
public class EquipmentDto implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private String classId; // When classId is set, classReference MUST also be set. Otherwise schema validation fails.

  @Schema
  private String classReference;

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private String model;

  @Schema
  private ContactDto manufacturer;

  @Schema
  private IdentificationListDto identifications;

  @Schema
  private SoftwareListDto software;

  @Schema
  private String certificateValue;

  @Schema
  private String certificateId;

  @Schema
  private LanguageSpecificStringsDto certificateReferral;

  @Schema
  private String certificateReferralId;

  @Schema
  private String certificateProcedure;
}
