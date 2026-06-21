package com.sprint.mission.discodeit.controller; // 본인의 패키지 경로

import com.sprint.mission.discodeit.service.basic.BasicSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

  private final BasicSseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

    UUID currentUserId = getCurrentUserId();

    UUID lastEventIdUuid = null;
    if (!lastEventId.isEmpty()) {
      lastEventIdUuid = UUID.fromString(lastEventId);
    }

    return sseService.connect(currentUserId, lastEventIdUuid);
  }

  private UUID getCurrentUserId() {
    return UUID.randomUUID();
  }
}
