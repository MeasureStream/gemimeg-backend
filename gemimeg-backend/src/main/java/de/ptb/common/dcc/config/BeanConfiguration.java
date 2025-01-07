package de.ptb.common.dcc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.xjc.generated.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static de.ptb.common.dcc.api.v1.json.SerializationUtils.createObjectMapper;

@Configuration
public class BeanConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    return createObjectMapper();
  }

  @Bean
  public ObjectFactory objectFactory() {
    return new ObjectFactory();
  }
}
