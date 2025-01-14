package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Schema
@Data
public class LangTextPair implements HasId, HasRefIds, HasRefTypes {

  @Schema
  private String id;

  @Schema
  private List<String> refIds;

  @Schema
  private List<String> refTypes;

  @Schema
  private String lang;

  @Schema
  private String text;

  @Nonnull
  public static LangTextPair of(@Nullable String lang, @Nullable String text) {
    LangTextPair langTextPair = new LangTextPair();
    langTextPair.setLang(lang);
    langTextPair.setText(text);
    return langTextPair;
  }
}
