package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSecurity {

  public boolean isOwner(Authentication authentication, UUID userId) {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    if (userDetails.getUserDto().role() == Role.ADMIN) {
      return true;
    }
    return userDetails.getUserDto().id().equals(userId);
  }
}