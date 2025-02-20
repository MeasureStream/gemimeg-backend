package de.ptb.common.dcc.service;

import de.ptb.common.dcc.api.v1.cache.ReadResponseDto;
import de.ptb.common.dcc.api.v1.cache.RequestDto;
import de.ptb.common.dcc.api.v1.cache.StoreResponseDto;
import de.ptb.common.dcc.config.CacheConfiguration;
import de.ptb.common.dcc.data.CacheItemRepository;
import de.ptb.common.dcc.model.CacheItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Optional;

import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.BASE_PATH_ID;
import static de.ptb.common.dcc.api.v1.CacheControllerRoutes.ID_PLACEHOLDER;

@Service
public class CacheService {

  private final CacheConfiguration configuration;
  private final CacheItemRepository repository;

  @Autowired
  public CacheService(CacheConfiguration configuration, CacheItemRepository repository) {
    this.configuration = configuration;
    this.repository = repository;
  }

  @Nonnull
  public StoreResponseDto store(@Nonnull RequestDto request) {
    CacheItem cacheItem = new CacheItem();
    cacheItem.setFileName(request.getFileName());
    cacheItem.setFileContent(request.getFileContent());
    cacheItem.setCallbackUrl(request.getCallbackUrl());
    cacheItem.setMimeType(request.getMimeType());
    cacheItem = repository.save(cacheItem);
    StoreResponseDto response = new StoreResponseDto();
    response.setRetrievalUrl(BASE_PATH_ID.replace(ID_PLACEHOLDER, cacheItem.getId().toString()));
    return response;
  }

  @Nonnull
  public Optional<ReadResponseDto> findById(@Nonnull Long id) {
    Optional<CacheItem> cacheItem = repository.findById(id);
    if (cacheItem.isPresent()) {
      ReadResponseDto readResponse = new ReadResponseDto();
      readResponse.setFileName(cacheItem.get().getFileName());
      readResponse.setFileContent(cacheItem.get().getFileContent());
      readResponse.setMimeType(cacheItem.get().getMimeType());
      repository.deleteById(id);
      return Optional.of(readResponse);
    }
    return Optional.empty();
  }

  @Scheduled(cron = "0 */5 * ? * *")
  public void deleteExpiredCacheItems() {
    final long milliseconds = configuration.getPersistLifespan().longValue() * 1000L;
    final Date expirationDate = new Date(System.currentTimeMillis() - milliseconds);
    repository.findByCreatedAtLessThan(expirationDate)
        .forEach(repository::delete);
  }
}
