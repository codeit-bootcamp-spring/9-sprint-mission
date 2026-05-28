package com.sprint.mission.discodeit.security;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserAccessGuard {

  public boolean isSelf(UUID userId, Authentication authentication) {
    return getCurrentUserId(authentication)
        .map(userId::equals)
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
