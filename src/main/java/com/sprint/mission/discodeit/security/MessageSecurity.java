package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
@RequiredArgsConstructor
public class MessageSecurity {

  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId) {
    if (messageId == null) {
      return false;
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return false;
    }

    Object principal = auth.getPrincipal();
    if (!(principal instanceof com.sprint.mission.discodeit.security.DiscodeitUserDetails)) {
      return false;
    }

    com.sprint.mission.discodeit.security.DiscodeitUserDetails details =
        (com.sprint.mission.discodeit.security.DiscodeitUserDetails) principal;

    java.util.UUID principalId = details.getUserDto().id();

    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(principalId))
        .orElse(false);
  }
}

