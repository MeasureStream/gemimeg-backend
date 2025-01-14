package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CacheItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheItemRepository extends MongoRepository<CacheItem, String> {
}
