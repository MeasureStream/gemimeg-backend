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
package de.ptb.common.dcc.service;

import de.ptb.common.dcc.api.v1.cache.ReadResponseDto;
import de.ptb.common.dcc.api.v1.cache.RequestDto;
import de.ptb.common.dcc.api.v1.cache.StoreResponseDto;
import de.ptb.common.dcc.config.CacheConfiguration;
import de.ptb.common.dcc.config.VersionConfiguration;
import de.ptb.common.dcc.data.CacheItemRepository;
import de.ptb.common.dcc.model.CacheItem;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

  private final Long cacheItemId = RandomUtils.secure().randomLong();
  private final Date createdAt = new Date(System.currentTimeMillis());
  @MockBean
  private VersionConfiguration versionConfiguration;
  @MockBean
  private CacheConfiguration configuration;
  @MockBean
  private CacheItemRepository repository;
  @Captor
  private ArgumentCaptor<Date> dateCaptor;
  private CacheService service;
  private CacheItem cacheItem;

  @BeforeEach
  void setUp() {
    when(versionConfiguration.getArtifactId()).thenReturn("cache-service-test");
    when(versionConfiguration.getVersion()).thenReturn("1.0.0");
    when(configuration.getPersistLifespan()).thenReturn(600);
    cacheItem = new CacheItem();
    cacheItem.setId(cacheItemId);
    cacheItem.setMimeType("text/plain");
    cacheItem.setFileName("test.txt");
    cacheItem.setCallbackUrl("http://test/callbackAction");
    cacheItem.setFileContent(Base64.getEncoder().encode("Für die Horde!".getBytes(StandardCharsets.UTF_8)));
    cacheItem.setCreatedAt(createdAt);
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
    assertEquals(BASE_PATH_ID.replace(ID_PLACEHOLDER, cacheItem.getId().toString()), actual.getRetrievalUrl());
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
    when(repository.findById(cacheItemId + 1)).thenReturn(Optional.empty());
    Optional<ReadResponseDto> actual = service.findById(cacheItemId + 1);
    assertNotNull(actual);
    assertFalse(actual.isPresent());
    verify(repository).findById(cacheItemId + 1);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void deleteExpiredCacheItems_Ok() throws NoSuchMethodException {
    when(repository.findByCreatedAtLessThan(any(Date.class))).thenReturn(List.of(cacheItem));
    Method deleteExpiredCacheItems = service.getClass().getMethod("deleteExpiredCacheItems");
    assertEquals("0 */5 * ? * *", deleteExpiredCacheItems.getAnnotation(Scheduled.class).cron());
    Date now = new Date(System.currentTimeMillis());
    service.deleteExpiredCacheItems();
    verify(repository).findByCreatedAtLessThan(dateCaptor.capture());
    assertEquals((now.getTime() - configuration.getPersistLifespan() * 1000L) / 100L,
        dateCaptor.getValue().getTime() / 100L);
    verify(repository).delete(cacheItem);
    verifyNoMoreInteractions(repository);
  }
}