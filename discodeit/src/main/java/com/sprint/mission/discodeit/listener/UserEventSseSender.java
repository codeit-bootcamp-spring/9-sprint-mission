package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserEventSseSender {

  private final SseService sseService;

  public void sendUserCreated(Collection<UUID> targetUserIds, UserDto userDto) {
    log.info("사용자 생성 발생 -> SSE 실시간 전송 (users.created)");

    sseService.send(targetUserIds, "users.created", userDto);
  }

  public void sendUserUpdated(Collection<UUID> targetUserIds, UserDto userDto) {
    log.info("사용자 정보 수정 발생 -> SSE 실시간 전송 (users.updated)");

    sseService.send(targetUserIds, "users.updated", userDto);
  }

  public void sendUserDeleted(Collection<UUID> targetUserIds, UserDto userDto) {
    log.info("사용자 삭제 발생 -> SSE 실시간 전송 (users.deleted)");

    sseService.send(targetUserIds, "users.deleted", userDto);
  }
}