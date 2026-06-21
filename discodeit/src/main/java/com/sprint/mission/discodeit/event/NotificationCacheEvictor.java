package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.config.CacheConfig;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NotificationCacheEvictor {

  private final CacheManager cacheManager;

  public void evictReceiver(UUID receiverId) {
    if (receiverId == null) {
      return;
    }

    Cache cache = cacheManager.getCache(CacheConfig.NOTIFICATIONS_BY_RECEIVER);
    if (cache != null) {
      cache.evict(receiverId);
    }
  }

  public void evictReceivers(Collection<UUID> receiverIds) {
    receiverIds.stream()
        .filter(Objects::nonNull)
        .distinct()
        .forEach(this::evictReceiver);
  }
}
