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
    assertEquals(55325500, mapperConfig.getSerializationFeatures());
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