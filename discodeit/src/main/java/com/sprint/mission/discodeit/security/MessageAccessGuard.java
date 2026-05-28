package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MessageAccessGuard {

  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId, Authentication authentication) {
    return getCurrentUserId(authentication)
        .map(authorId -> messageRepository.existsByIdAndAuthor_Id(messageId, authorId))
        .orElse(false);
  }

  private Optional<UUID> getCurrentUserId(Authentication authentication) {
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return Optional.empty();
    }
    return Optional.of(userDetails.getUserDto().id());
  }
}
