package de.ptb.common.dcc.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema
public class AdministrativeDataDto {

  @Schema
  private SoftwareListDto dccSoftware;

  @Schema
  private ContactDto customer;

  @Schema
  private CalibrationLaboratoryDto calibrationLaboratory;

  @Schema
  private ItemListDto items;

  @Schema
  private String uniqueIdentifier;

  @Schema
  private String countryCode;

  @Schema
  private Set<String> languageCodes;

  @Schema
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate receiptDate;

  @Schema
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate startDate;

  @Schema
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  @Schema
  private String performanceLocation;

  @Schema
  private ContactListDto responsiblePersons;

  @Schema
  private StatementListDto statements;
}
