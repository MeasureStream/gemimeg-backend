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
