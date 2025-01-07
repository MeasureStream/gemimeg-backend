package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class LocationDto implements HasId {

  @Schema
  private String id;

  @Schema
  private String countryCode;

  @Schema
  private String stateCode;

  @Schema
  private String city;

  @Schema
  private String postalCode;

  @Schema
  private String street;

  @Schema
  private String houseNumber;

  @Schema
  private String poBox;

  @Schema
  private RichContentDto additionalInformation;
}
