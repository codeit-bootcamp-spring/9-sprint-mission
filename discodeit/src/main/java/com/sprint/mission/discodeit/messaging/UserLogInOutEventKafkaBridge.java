package com.sprint.mission.discodeit.messaging;

import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserLogInOutEventKafkaBridge {

  private final KafkaTemplate<String, Object> kafkaTemplate;

  @EventListener
  public void handleLocalEvent(UserLogInOutEvent event) {
    kafkaTemplate.send("user-login-out-topic", event);
  }
}
