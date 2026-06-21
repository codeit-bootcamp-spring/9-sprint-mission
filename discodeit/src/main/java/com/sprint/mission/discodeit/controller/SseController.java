package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class SseController {

  private static final String LAST_EVENT_ID_HEADER = "Last-Event-ID";

  private final SseService sseService;

  @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      Authentication authentication,
      @RequestHeader(value = LAST_EVENT_ID_HEADER, required = false) UUID lastEventIdHeader,
      @RequestParam(value = "lastEventId", required = false) UUID lastEventIdParam
  ) {
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      throw new AccessDeniedException("SSE connection requires authentication.");
    }
    UUID lastEventId = lastEventIdHeader != null ? lastEventIdHeader : lastEventIdParam;
    return sseService.connect(userDetails.getUserDto().id(), lastEventId);
  }
}
