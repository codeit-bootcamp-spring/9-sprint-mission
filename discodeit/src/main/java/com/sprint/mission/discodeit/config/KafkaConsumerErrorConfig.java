package com.sprint.mission.discodeit.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@ConditionalOnProperty(
    prefix = "discodeit.notification.kafka",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class KafkaConsumerErrorConfig {

  @Bean
  public DefaultErrorHandler defaultErrorHandler(
      KafkaTemplate<String, String> kafkaTemplate,
      @Value("${discodeit.notification.kafka.retry.interval-millis:1000}") long intervalMillis,
      @Value("${discodeit.notification.kafka.retry.max-attempts:3}") long maxAttempts
  ) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
        kafkaTemplate,
        this::deadLetterTopic
    );
    return new DefaultErrorHandler(recoverer, new FixedBackOff(intervalMillis, maxAttempts));
  }

  private TopicPartition deadLetterTopic(
      ConsumerRecord<?, ?> record,
      Exception exception
  ) {
    return new TopicPartition(record.topic() + ".DLT", record.partition());
  }
}
