package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.service.SessionService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicSessionService implements SessionService {

  private final JwtRegistry jwtRegistry;

  @Override
  public Set<UUID> getOnlineUserIds() {
    return jwtRegistry.getAllOnlineUserIds();
  }

  @Override
  public boolean isUserOnline(UUID userId) {
    return jwtRegistry.hasActiveJwtInformationByUserId(userId);
  }
}
