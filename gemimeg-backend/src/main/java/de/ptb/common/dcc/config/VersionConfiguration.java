package de.ptb.common.dcc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "mvn")
@PropertySource(value = "classpath:version.yml", factory = YamlPropertySourceFactory.class)
public class VersionConfiguration {

  private String pathPart;
  private String artifactId;
  private String version;
  private String timestamp;
}
