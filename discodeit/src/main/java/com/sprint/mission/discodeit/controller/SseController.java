package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping("/api/sse")
  public SseEmitter connect(
      @RequestHeader(value = "Last-Event-ID", required = false)
      UUID lastEventId
  ) {

    UUID receiverId = getCurrentUserId();

    return sseService.connect(
        receiverId,
        lastEventId
    );
  }

  private UUID getCurrentUserId() {

    // 기존 Security 구조에 맞게 수정
    throw new UnsupportedOperationException();
  }
}