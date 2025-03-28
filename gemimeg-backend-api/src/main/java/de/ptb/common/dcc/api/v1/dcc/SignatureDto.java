/**
 * Copyright 2025 Physikalisch-Technische Bundesanstalt
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS “AS IS” AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
