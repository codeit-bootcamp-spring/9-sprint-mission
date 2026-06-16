package com.sprint.mission.discodeit.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

  // 캐시 이름 상수 — 서비스에서 오타 없이 참조하기 위해 한 곳에서 관리
  public static final String USER_LIST            = "userList";
  public static final String USER_CHANNEL_LIST    = "userChannelList";
  public static final String USER_NOTIFICATION_LIST = "userNotificationList";

  @Bean
  public Caffeine<Object, Object> caffeineSpec() {
    return Caffeine.newBuilder()
        .maximumSize(500)          // 캐시 최대 항목 수
        .expireAfterWrite(10, TimeUnit.MINUTES)  // 쓰기 후 10분 TTL
        .recordStats();            // 캐시 히트율 등 통계 수집
  }

  @Bean
  public CacheManager cacheManager(Caffeine<Object, Object> caffeineSpec) {
    CaffeineCacheManager manager = new CaffeineCacheManager();
    manager.setCaffeine(caffeineSpec);
    manager.setCacheNames(List.of(USER_LIST, USER_CHANNEL_LIST, USER_NOTIFICATION_LIST));
    return manager;
  }
}
