package com.sprint.mission.discodeit.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

  public static final String CHANNELS_BY_USER = "channelsByUser";
  public static final String NOTIFICATIONS_BY_RECEIVER = "notificationsByReceiver";
  public static final String USERS = "users";
}
