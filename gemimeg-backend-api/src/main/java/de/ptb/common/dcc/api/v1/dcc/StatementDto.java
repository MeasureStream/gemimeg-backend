package de.ptb.common.dcc.api.v1.dcc;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.PatternProperties;
import io.swagger.v3.oas.annotations.media.PatternProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.MatchesPattern;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema
public class StatementDto implements HasId, HasRefIds, HasRefTypes {

  public static final String PERIOD_PATTERN = "^P(?!$)(\\d+(?:\\.\\d+)?Y)?(\\d+(?:\\.\\d+)?M)?(\\d+(?:\\.\\d+)?W)?(\\" +
      "d+(?:\\.\\d+)?D)?(T(?=\\d)(\\d+(?:\\.\\d+)?H)?(\\d+(?:\\.\\d+)?M)?(\\d+(?:\\.\\d+)?S)?)?$";

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private DataListDto data;

  @Schema
  private List<String> countryCodes;

  @Schema
  private String convention;

  @Schema
  private Boolean traceable;

  @Schema
  private String conformity;

  @Schema
  private List<String> conformityXMLList;

  @Schema
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate date;

  @Schema
  @MatchesPattern(PERIOD_PATTERN)
  @PatternProperties(
      @PatternProperty(regex = PERIOD_PATTERN)
  )
  private String period; // according to https://en.wikipedia.org/wiki/ISO_8601#Time_intervals

  @Schema
  private LanguageSpecificStringsDto name;

  @Schema
  private List<String> norms;

  @Schema
  private List<String> references;

  @Schema
  private RichContentDto description;

  @Schema
  private RichContentDto declaration;

  @Schema
  private Boolean valid;

  @Schema
  private List<Boolean> validXMLList;

  @Schema
  private String nonSIDefinition;

  @Schema
  private String nonSIUnit;

  @Schema
  private LocationDto location;

  @Schema
  private ContactDto responsibleAuthority;
}
