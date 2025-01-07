package de.ptb.common.dcc.api.v1.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static de.ptb.common.dcc.api.v1.json.SerializationUtils.DELIMITER;
import static de.ptb.common.dcc.api.v1.json.SerializationUtils.ID;
import static de.ptb.common.dcc.api.v1.json.SerializationUtils.REF_ID;
import static de.ptb.common.dcc.api.v1.json.SerializationUtils.REF_TYPES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class SerializationUtilsTest {

  private JsonGenerator generator;

  @BeforeEach
  void setUp() {
    generator = mock(JsonGenerator.class, RETURNS_DEEP_STUBS);
  }

  @Test
  void createObjectMapper_Ok() {
    ObjectMapper objectMapper = SerializationUtils.createObjectMapper();
    assertNotNull(objectMapper);
    assertFalse(objectMapper.getRegisteredModuleIds().isEmpty());
    assertTrue(objectMapper.getRegisteredModuleIds().contains("jackson-datatype-jsr310"));
    SerializationConfig mapperConfig = objectMapper.getSerializationConfig();
    assertNotNull(mapperConfig);
    assertEquals(21771068, mapperConfig.getSerializationFeatures());
  }

  @Test
  void writeIdAndRefTypes_Ok() throws IOException {
    SerializationUtils.writeIdAndRefTypes("123456", List.of("123", "456"), List.of("abc", "def"), generator);
    verify(generator).writeStringField(ID, "123456");
    verify(generator).writeStringField(REF_TYPES, "123" + DELIMITER + "456");
    verify(generator).writeStringField(REF_ID, "abc" + DELIMITER + "def");
    verifyNoMoreInteractions(generator);
  }

  @Test
  void createMD5Hash_Ok() throws NoSuchAlgorithmException {
    String md5Hash = SerializationUtils.createMD5Hash("123456");
    assertEquals("e10adc3949ba59abbe56e057f20f883e", md5Hash);
  }
}