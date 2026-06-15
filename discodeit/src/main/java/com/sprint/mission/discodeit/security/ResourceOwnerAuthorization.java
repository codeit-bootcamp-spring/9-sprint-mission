package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceOwnerAuthorization {

  private final MessageRepository messageRepository;

  public boolean isSelf(UUID userId, Authentication authentication) {
    UUID currentUserId = getCurrentUserId(authentication);
    return Objects.equals(userId, currentUserId);
  }

  public boolean isMessageAuthor(UUID messageId, Authentication authentication) {
    UUID currentUserId = getCurrentUserId(authentication);
    UUID authorId = messageRepository.findAuthorIdById(messageId)
        .orElseThrow(() -> MessageNotFoundException.withId(messageId));
    return Objects.equals(authorId, currentUserId);
  }

  private UUID getCurrentUserId(Authentication authentication) {
    if (authentication == null
        || !(authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails)) {
      return null;
    }
    return userDetails.getUserDto().id();
  }
}
