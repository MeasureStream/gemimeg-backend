package de.ptb.common.dcc.data;

import de.ptb.common.dcc.model.CacheItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CacheItemRepository extends JpaRepository<CacheItem, Long> {

  List<CacheItem> findByCreatedAtLessThan(Date createdAt);
}
