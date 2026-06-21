package com.sprint.mission.discodeit.messaging;

import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLogInOutKafkaConsumer {

  @KafkaListener(
      topics = "user-login-out-topic",
      groupId = "user-status-group-#{T(java.util.UUID).randomUUID().toString()}"
  )
  public void consumeUserLogInOutEvent(UserLogInOutEvent event) {
    log.info("📥 [카프카 수신] 서버 인스턴스가 이벤트를 정상 수신했습니다. 유저 ID: {}, 로그인 여부: {}",
        event.getUserId(), event.isLogin());
  }
}
