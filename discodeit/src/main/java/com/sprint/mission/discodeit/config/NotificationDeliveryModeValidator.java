package com.sprint.mission.discodeit.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationDeliveryModeValidator {

  private final boolean kafkaEnabled;
  private final boolean springEventListenerEnabled;

  public NotificationDeliveryModeValidator(
      @Value("${discodeit.notification.kafka.enabled:true}") boolean kafkaEnabled,
      @Value("${discodeit.notification.spring-event-listener.enabled:false}")
      boolean springEventListenerEnabled
  ) {
    this.kafkaEnabled = kafkaEnabled;
    this.springEventListenerEnabled = springEventListenerEnabled;
  }

  @PostConstruct
  void validate() {
    if (kafkaEnabled && springEventListenerEnabled) {
      throw new IllegalStateException(
          "Only one notification delivery mode can be enabled. "
              + "Disable either discodeit.notification.kafka.enabled "
              + "or discodeit.notification.spring-event-listener.enabled."
      );
    }
  }
}
