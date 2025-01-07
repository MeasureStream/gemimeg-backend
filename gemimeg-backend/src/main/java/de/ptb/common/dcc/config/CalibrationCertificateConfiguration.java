package de.ptb.common.dcc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "common.dcc")
public class CalibrationCertificateConfiguration {

  private String schemaUrl;
  private String namespaceUri;
}
