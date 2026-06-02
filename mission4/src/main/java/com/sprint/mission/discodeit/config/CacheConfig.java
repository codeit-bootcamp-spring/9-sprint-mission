package com.sprint.mission.discodeit.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(Arrays.asList(
        createCache("channel", 600, 100),
        createCache("notifications", 300, 500),
        createCache("users", 1800, 50)
    ));
    return cacheManager;
  }

  private CaffeineCache createCache(String name, int seconds, int maxSize) {
    return new CaffeineCache(
        name, Caffeine.newBuilder()
        .recordStats()
        .expireAfterWrite(seconds, TimeUnit.SECONDS)
        .maximumSize(maxSize)
        .build()
    );
  }

}
