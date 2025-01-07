package de.ptb.common.dcc.controller;

import de.ptb.common.dcc.api.v1.dto.VersionDto;
import de.ptb.common.dcc.config.VersionConfiguration;
import de.ptb.common.http.BadRequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static de.ptb.common.dcc.api.v1.VersionControllerRoutes.PATH_PART_NAME;
import static de.ptb.common.dcc.api.v1.VersionControllerRoutes.VERSION_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Tag(name = "VersionController")
public class VersionController {

  private final VersionConfiguration versionConfiguration;

  @Autowired
  public VersionController(VersionConfiguration versionConfiguration) {
    this.versionConfiguration = versionConfiguration;
  }

  @Operation(description = "Get the version information of the service.")
  @GetMapping(path = VERSION_PATH, produces = APPLICATION_JSON_VALUE)
  public VersionDto getVersion(@PathVariable(name = PATH_PART_NAME) String pathPart) {
    if (StringUtils.equalsIgnoreCase(versionConfiguration.getPathPart(), pathPart)) {
      VersionDto version = new VersionDto();
      version.setArtifactId(versionConfiguration.getArtifactId());
      version.setVersion(versionConfiguration.getVersion());
      version.setTimestamp(versionConfiguration.getTimestamp());
      return version;
    } else {
      throw new BadRequestStatus("Service path not provided, or not properly configured in version.yml.");
    }
  }
}
