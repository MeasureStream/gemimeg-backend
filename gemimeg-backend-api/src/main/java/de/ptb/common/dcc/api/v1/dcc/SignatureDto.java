package de.ptb.common.dcc.api.v1.dcc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Schema
public class SignatureDto implements HasId {

  @Schema
  private String id;

  @Schema
  private SignatureValue value;

  @Schema
  private SignedInfo signedInfo;

  @Schema
  private KeyInfo keyInfo;

  @Schema
  private List<Content> contentList;

  @Data
  @Schema
  public static class SignatureValue implements HasId {

    @Schema
    private String id;

    @Schema
    private byte[] value;
  }

  @Data
  @Schema
  public static class SignedInfo implements HasId {

    @Schema
    private String id;

    @Schema
    private List<Object> canonicalizationContent;

    @Schema
    private String canonicalizationAlgorithm;

    @Schema
    private List<Object> methodContent;

    @Schema
    private String methodAlgorithm;
  }

  @Data
  @Schema
  public static class KeyInfo implements HasId {

    public static String KEY_VALUE_KEY = "KeyValue";
    public static String PGP_KEY = "PGPData";
    public static String RETRIEVAL_METHOD_KEY = "RetrievalMethod";
    public static String SPKI_KEY = "SPKIData";
    public static String X509_KEY = "X509Data";
    public static String STRING_KEY = "String";

    @Schema
    private String id;

    @Schema
    private Map<String, Object> content;

    @Nonnull
    public Optional<Object> getContentObject(String key) {
      if (content == null) {
        return Optional.empty();
      }
      return Optional.ofNullable(content.get(key));
    }

    @Data
    @Schema
    public static class RetrievalMethod {

      @Schema
      private String uri;

      @Schema
      private String type;

      @Schema
      private List<String> transformAlgorithms;

      @Schema
      private List<List<Object>> transforms;
    }
  }

  @Data
  @Schema
  public static class Content implements HasId {

    @Schema
    private String id;

    @Schema
    private String encoding;

    @Schema
    private String mimeType;

    @Schema
    private List<Object> content;
  }
}
