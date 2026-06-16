package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.service.basic.BasicSseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

  private final BasicSseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      @AuthenticationPrincipal UserDetails userDetails, // 현재 로그인한 유저 정보
      @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

    UUID receiverId = UUID.fromString(userDetails.getUsername());

    return sseService.connect(receiverId,
        lastEventId.isEmpty() ? null : UUID.fromString(lastEventId));
  }
}



