package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.init.RedisJwtRegistry;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class JwtConfig {

  @Bean
  public JwtRegistry jwtRegistry(
      JwtTokenProvider jwtTokenProvider,
      ApplicationEventPublisher eventPublisher,
      RedisTemplate<String, Object> redisTemplate,
      RedisLockProvider redisLockProvider) {

    return new RedisJwtRegistry(5, jwtTokenProvider, eventPublisher, redisTemplate,
        redisLockProvider);
  }
}


