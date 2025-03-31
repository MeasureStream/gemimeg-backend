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

import de.ptb.common.dcc.api.v1.cache.ReadResponseDto;
import de.ptb.common.dcc.api.v1.cache.RequestDto;
import de.ptb.common.dcc.api.v1.cache.StoreResponseDto;
import de.ptb.common.dcc.service.CacheService;
import de.ptb.common.http.NotFoundStatus;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.BASE_PATH;
import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.BASE_PATH_ID;
import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.ID_PARAM_NAME;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Tag(name = "CacheController")
@OpenAPIDefinition(servers = {
    @Server(url = "http://localhost:10001", description = "GEMIMEG Backend powered by OP-Layer")
})
public class CacheController {

  private final CacheService service;

  @Autowired
  public CacheController(CacheService service) {
    this.service = service;
  }

  @Operation(description = "Stores a file in the cache.")
  @PostMapping(path = BASE_PATH, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public StoreResponseDto store(@RequestBody RequestDto request) {
    return service.store(request);
  }

  @Operation(description = "Gets the cached item by its ID.")
  @GetMapping(path = BASE_PATH_ID, produces = APPLICATION_JSON_VALUE)
  public ReadResponseDto findById(@PathVariable(name = ID_PARAM_NAME) Long id) {
    return service.findById(id)
        .orElseThrow(() -> new NotFoundStatus("Cached file with ID " + id + " could not be found."));
  }
}
