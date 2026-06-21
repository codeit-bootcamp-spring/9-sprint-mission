package com.sprint.mission.discodeit.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String,Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      @Qualifier("redisSerializer")
      GenericJackson2JsonRedisSerializer serializer
  ) {

    RedisTemplate<String,Object> template =
        new RedisTemplate<>();

    template.setConnectionFactory(connectionFactory);

    template.setKeySerializer(
        new StringRedisSerializer()
    );

    template.setValueSerializer(serializer);

    template.afterPropertiesSet();

    return template;
  }

  @Bean("redisSerializer")
  public GenericJackson2JsonRedisSerializer redisSerializer(
      ObjectMapper objectMapper
  ) {

    ObjectMapper redisMapper = objectMapper.copy();

    redisMapper.activateDefaultTyping(
        LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.EVERYTHING,
        JsonTypeInfo.As.PROPERTY
    );

    return new GenericJackson2JsonRedisSerializer(redisMapper);
  }
}