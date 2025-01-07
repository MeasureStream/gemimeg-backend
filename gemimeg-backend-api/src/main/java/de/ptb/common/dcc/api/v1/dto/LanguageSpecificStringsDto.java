package de.ptb.common.dcc.api.v1.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema
public class LanguageSpecificStringsDto implements HasId, HasRefIds {

  public static final String LANGUAGE_DE = "de";

  @SuppressWarnings("unused")
  public static final String LANGUAGE_EN = "gb"; // not 'en' due to frontend-specific issue (flag display).

  @SuppressWarnings("unused")
  public static final String LANGUAGE_FR = "fr";

  @SuppressWarnings("unused")
  public static final String LANGUAGE_ES = "es";

  @SuppressWarnings("unused")
  public static final String LANGUAGE_IT = "it";

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<LangTextPair> content = new ArrayList<>(1);

  @Nonnull
  public LanguageSpecificStringsDto add(@Nonnull String language, @Nonnull String text) {
    content.add(LangTextPair.of(language, text));
    return this;
  }

  @Nonnull
  public LanguageSpecificStringsDto add(@Nonnull String text) {
    return add(LANGUAGE_DE, text);
  }
}
