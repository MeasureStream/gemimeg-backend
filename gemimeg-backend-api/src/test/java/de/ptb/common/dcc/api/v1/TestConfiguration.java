package de.ptb.common.dcc.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import static de.ptb.common.dcc.api.v1.json.SerializationUtils.createObjectMapper;

@SpringBootConfiguration
@ComponentScan("de.ptb.common.dcc.api.v1")
public class TestConfiguration {

  public final static String REST_URI = "https://test";

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = createObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  @Bean
  public ICalibrationCertificateClientConfiguration clientConfiguration() {
    return () -> REST_URI;
  }
}
