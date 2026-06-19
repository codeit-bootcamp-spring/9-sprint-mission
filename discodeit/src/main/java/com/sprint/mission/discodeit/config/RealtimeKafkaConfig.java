package com.sprint.mission.discodeit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "discodeit.realtime.kafka",
    name = "enabled",
    havingValue = "true"
)
public class RealtimeKafkaConfig {

  @Bean
  public String realtimeKafkaConsumerGroupId(
      @Value("${HOSTNAME:${random.uuid}}") String instanceId
  ) {
    return "discodeit-realtime-" + instanceId;
  }
}
