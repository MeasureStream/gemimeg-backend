package de.ptb.common.dcc.api.v1.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

public class SerializationUtils {

  public static final String ID = "id";
  public static final String REF_TYPES = "refTypes";
  public static final String REF_ID = "refId";
  public static final String DELIMITER = ";";

  @Nonnull
  public static ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  public static void writeIdAndRefTypes(@Nullable String id, @Nullable List<String> refTypes,
                                        @Nullable List<Object> refId, @Nonnull JsonGenerator generator)
      throws IOException {
    if (StringUtils.isNotBlank(id)) {
      generator.writeStringField(ID, id);
    }
    if (refTypes != null && !refTypes.isEmpty()) {
      generator.writeStringField(REF_TYPES, String.join(DELIMITER, refTypes));
    }
    if (refId != null && !refId.isEmpty()) {
      generator.writeStringField(REF_ID, refId.stream()
          .map(Object::toString)
          .collect(Collectors.joining(DELIMITER)));
    }
  }

  @Nonnull
  public static String createMD5Hash(@Nonnull String text) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    return convertToHex(messageDigest.digest(text.getBytes()));
  }

  private static String convertToHex(@Nonnull byte[] data) {
    BigInteger bigint = new BigInteger(1, data);
    String hexText = bigint.toString(16);
    while (hexText.length() < 32) {
      hexText = "0".concat(hexText);
    }
    return hexText;
  }
}
