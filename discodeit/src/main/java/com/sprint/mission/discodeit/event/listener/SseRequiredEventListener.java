package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.event.message.UserLogInOutEvent;
import com.sprint.mission.discodeit.event.sse.BinaryContentStatusUpdatedEvent;
import com.sprint.mission.discodeit.event.sse.ChannelEvent;
import com.sprint.mission.discodeit.event.sse.NotificationCreatedEvent;
import com.sprint.mission.discodeit.event.sse.UserEvent;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 분산 환경에서는 이 SSE 연결을 보유한 인스턴스와 이벤트가 발생한 인스턴스가 다를 수
 * 있어 로컬 이벤트만으로는 전달되지 않는다. 대신 event.kafka.SseRequiredTopicListener가
 * Kafka 팬아웃 토픽을 구독해 모든 인스턴스에서 동일하게 SSE를 push한다.
 */
@RequiredArgsConstructor
// @Component
public class SseRequiredEventListener {

  private final SseService sseService;
  private final UserService userService;

  @Async("eventTaskExecutor")
  @EventListener
  public void on(NotificationCreatedEvent event) {
    event.getNotifications().forEach(dto ->
        sseService.send(
            Set.of(dto.receiverId()),
            "notifications.created",
            dto
        )
    );
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(BinaryContentStatusUpdatedEvent event) {
    sseService.broadcast("binaryContents.updated", event.getBinaryContentDto());
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(ChannelEvent event) {
    String eventName = "channels." + event.getAction().name().toLowerCase();
    sseService.broadcast(eventName, event.getChannelDto());
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(UserEvent event) {
    String eventName = "users." + event.getAction().name().toLowerCase();
    sseService.broadcast(eventName, event.getUserDto());
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(UserLogInOutEvent event) {
    UserDto dto = userService.find(event.getUserId());
    sseService.broadcast("users.updated", dto);
  }
}
