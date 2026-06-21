package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader,
      @RequestParam(value = "lastEventId", required = false) String lastEventIdParam,
      HttpServletResponse response) {

    // Nginx 프록시 등에서 버퍼링하는 것을 방지하기 위한 헤더 설정
    response.setHeader("X-Accel-Buffering", "no");

    UUID userId = userDetails.getUserDto().id();
    log.info("SSE connection request from user: {}, Last-Event-ID (Header): {}, Last-Event-ID (Param): {}",
        userId, lastEventIdHeader, lastEventIdParam);

    String rawLastEventId = StringUtils.hasText(lastEventIdHeader) ? lastEventIdHeader : lastEventIdParam;
    UUID lastEventId = null;
    if (StringUtils.hasText(rawLastEventId)) {
      try {
        lastEventId = UUID.fromString(rawLastEventId);
      } catch (IllegalArgumentException e) {
        log.warn("Invalid Last-Event-ID format: {}", rawLastEventId);
      }
    }

    return sseService.connect(userId, lastEventId);
  }
}
