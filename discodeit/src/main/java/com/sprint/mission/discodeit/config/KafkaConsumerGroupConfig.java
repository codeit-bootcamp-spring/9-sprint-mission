package com.sprint.mission.discodeit.config;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerGroupConfig {

  @Bean("realtimeKafkaGroupId")
  public String realtimeKafkaGroupId(
      @Value("${HOSTNAME:}") String hostname
  ) {
    String instanceId = hostname == null || hostname.isBlank()
        ? UUID.randomUUID().toString()
        : hostname;
    return "discodeit-realtime-" + instanceId;
  }
}
