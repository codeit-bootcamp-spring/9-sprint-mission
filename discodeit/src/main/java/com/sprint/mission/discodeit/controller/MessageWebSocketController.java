package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void create(@Valid @Payload MessageCreateRequest request, Principal principal) {
    DiscodeitUserDetails userDetails = resolveUserDetails(principal);
    if (!userDetails.getUserDto().id().equals(request.authorId())) {
      throw new AccessDeniedException("Message author does not match authenticated user");
    }

    log.debug("STOMP /pub/messages - create message: channelId={}, authorId={}",
        request.channelId(), request.authorId());
    messageService.create(request, List.of());
  }

  private DiscodeitUserDetails resolveUserDetails(Principal principal) {
    if (principal instanceof Authentication authentication
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      return userDetails;
    }
    throw new BadCredentialsException("WebSocket authentication is required");
  }
}
