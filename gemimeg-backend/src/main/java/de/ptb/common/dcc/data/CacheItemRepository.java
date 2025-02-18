package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CacheItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CacheItemRepository extends MongoRepository<CacheItem, String> {

  List<CacheItem> findByCreatedAtLessThan(Date createdAt);
}
