package com.sprint.mission.discodeit.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "discodeit.kafka.enabled", havingValue = "true")
public class KafkaTopicConfig {

  @Bean
  public NewTopic messageCreatedTopic() {
    return TopicBuilder.name("discodeit.MessageCreatedEvent").partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic roleUpdatedTopic() {
    return TopicBuilder.name("discodeit.RoleUpdatedEvent").partitions(1).replicas(1).build();
  }

  @Bean
  public NewTopic s3UploadFailedTopic() {
    return TopicBuilder.name("discodeit.S3UploadFailedEvent").partitions(1).replicas(1).build();
  }
}
