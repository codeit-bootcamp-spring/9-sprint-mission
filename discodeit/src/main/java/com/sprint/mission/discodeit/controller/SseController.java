package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping("/api/sse")
  public SseEmitter subscribe(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId
  ) {
    UUID userId = userDetails.getUserDto().id();
    return sseService.connect(userId, lastEventId);
  }
}
