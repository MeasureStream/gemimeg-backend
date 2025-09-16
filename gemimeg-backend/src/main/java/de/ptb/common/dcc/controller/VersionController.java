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
package de.ptb.common.dcc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.dcc.VersionDto;
import de.ptb.common.dcc.config.VersionConfiguration;
import de.ptb.common.http.BadRequestStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static de.ptb.common.dcc.api.v1.VersionControllerRoutes.PATH_PART_NAME;
import static de.ptb.common.dcc.api.v1.VersionControllerRoutes.VERSION_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Tag(name = "VersionController")
public class VersionController {

  private final VersionConfiguration versionConfiguration;
  private final ObjectMapper objectMapper;

  @Autowired
  public VersionController(VersionConfiguration versionConfiguration, ObjectMapper objectMapper) {
    this.versionConfiguration = versionConfiguration;
    this.objectMapper = objectMapper;
  }

  @Operation(description = "Get the version information of the service.")
  @GetMapping(path = VERSION_PATH, produces = APPLICATION_JSON_VALUE)
  public VersionDto getVersion(@PathVariable(name = PATH_PART_NAME) String pathPart) throws IOException {
    if (Strings.CI.equals(versionConfiguration.getPathPart(), pathPart)) {
      VersionDto version = new VersionDto();
      version.setArtifactId(versionConfiguration.getArtifactId());
      version.setVersion(versionConfiguration.getVersion());
      version.setTimestamp(versionConfiguration.getTimestamp());
      CurrentVersionJson currentVersionJson = objectMapper.readValue(IOUtils.resourceToString("version.json", StandardCharsets.UTF_8),
          CurrentVersionJson.class);
      if (Strings.CI.equals(currentVersionJson.getName(), version.getArtifactId())) {
        version.setTag(currentVersionJson.getVersion());
      }
      return version;
    } else {
      throw new BadRequestStatus("Service path not provided, or not properly configured in version.yml.");
    }
  }
}
