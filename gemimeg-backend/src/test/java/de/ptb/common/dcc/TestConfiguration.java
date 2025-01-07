/*
 * Copyright (c) 2022 Physikalisch-Technische Bundesanstalt
 */

package de.ptb.common.dcc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import static de.ptb.common.dcc.api.v1.json.SerializationUtils.createObjectMapper;

@Configuration
public class TestConfiguration {

  @Bean
  public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
    Jackson2ObjectMapperBuilder result = new Jackson2ObjectMapperBuilder();
    result.configure(createObjectMapper());
    return result;
  }

  @Bean
  public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    return new MappingJackson2HttpMessageConverter(jackson2ObjectMapperBuilder().build());
  }
}
