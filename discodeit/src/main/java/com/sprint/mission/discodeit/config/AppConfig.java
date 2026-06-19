package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.redis.RedisLockProvider;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.RedisJwtRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AppConfig {

  @Bean
  public RedisJwtRegistry redisJwtRegistry(
      JwtTokenProvider jwtTokenProvider,
      ApplicationEventPublisher eventPublisher,
      RedisTemplate<String, Object> redisTemplate,
      RedisLockProvider redisLockProvider
  ) {
    return new RedisJwtRegistry(
        3,  // maxActiveJwtCount: 동시 로그인 허용 최대 개수
        jwtTokenProvider,
        eventPublisher,
        redisTemplate,
        redisLockProvider
    );
  }
}