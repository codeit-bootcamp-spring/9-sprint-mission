package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SessionService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicSessionService implements SessionService {

  private final SessionRegistry sessionRegistry;

  @Override
  public Set<String> getOnlineUsernames() {
    return sessionRegistry.getAllPrincipals().stream()
        .filter(principal -> principal instanceof DiscodeitUserDetails)
        .map(principal -> ((DiscodeitUserDetails) principal).getUsername())
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isUserOnline(String username) {
    DiscodeitUserDetails searchKey = new DiscodeitUserDetails(username);
    List<SessionInformation> sessions = sessionRegistry.getAllSessions(searchKey, false);
    return !sessions.isEmpty();
  }
}
