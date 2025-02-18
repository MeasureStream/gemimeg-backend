package de.ptb.common.dcc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ptb.common.dcc.api.v1.cache.ReadResponseDto;
import de.ptb.common.dcc.api.v1.cache.RequestDto;
import de.ptb.common.dcc.api.v1.cache.StoreResponseDto;
import de.ptb.common.dcc.config.CacheConfiguration;
import de.ptb.common.dcc.config.VersionConfiguration;
import de.ptb.common.dcc.data.CacheItemRepository;
import de.ptb.common.dcc.model.CacheItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.BASE_PATH_ID;
import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.ID_PLACEHOLDER;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
class CacheServiceTest {

  @MockBean
  private VersionConfiguration versionConfiguration;

  @MockBean
  private CacheConfiguration configuration;

  @MockBean
  private CacheItemRepository repository;

  @Autowired
  private ObjectMapper objectMapper;

  private CacheService service;
  private CacheItem cacheItem;

  @BeforeEach
  void setUp() {
    when(versionConfiguration.getArtifactId()).thenReturn("cache-service-test");
    when(versionConfiguration.getVersion()).thenReturn("1.0.0");
    when(configuration.getPersistLifespan()).thenReturn(600);
    cacheItem = new CacheItem();
    cacheItem.setId(UUID.randomUUID().toString());
    cacheItem.setMimeType("text/plain");
    cacheItem.setFileName("test.txt");
    cacheItem.setCallbackUrl("http://test/callbackAction");
    cacheItem.setFileContent(Base64.getEncoder().encode("Für die Horde!".getBytes(StandardCharsets.UTF_8)));
    service = new CacheService(configuration, repository);
  }

  @Test
  void store_Ok() {
    RequestDto request = new RequestDto();
    request.setFileName(cacheItem.getFileName());
    request.setMimeType(cacheItem.getMimeType());
    request.setFileContent(cacheItem.getFileContent());
    request.setCallbackUrl(cacheItem.getCallbackUrl());
    when(repository.save(any())).thenReturn(cacheItem);
    StoreResponseDto actual = service.store(request);
    assertNotNull(actual);
    assertEquals(BASE_PATH_ID.replace(ID_PLACEHOLDER, cacheItem.getId()), actual.getRetrievalUrl());
  }

  @Test
  void findById_Ok() {
    when(repository.findById(cacheItem.getId())).thenReturn(Optional.of(cacheItem));
    Optional<ReadResponseDto> actual = service.findById(cacheItem.getId());
    assertNotNull(actual);
    assertTrue(actual.isPresent());
    assertEquals(cacheItem.getMimeType(), actual.get().getMimeType());
    assertEquals(cacheItem.getFileName(), actual.get().getFileName());
    assertArrayEquals(cacheItem.getFileContent(), actual.get().getFileContent());
    verify(repository).findById(cacheItem.getId());
    verify(repository).deleteById(cacheItem.getId());
  }

  @Test
  void findById_NotFound() {
    when(repository.findById("missingId")).thenReturn(Optional.empty());
    Optional<ReadResponseDto> actual = service.findById("missingId");
    assertNotNull(actual);
    assertFalse(actual.isPresent());
    verify(repository).findById("missingId");
    verifyNoMoreInteractions(repository);
  }
}