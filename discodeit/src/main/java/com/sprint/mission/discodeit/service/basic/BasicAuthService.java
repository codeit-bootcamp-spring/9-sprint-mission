package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

  private final SessionRegistry sessionRegistry;

  @Override
  public void invalidateUserSessions(String username) {
    DiscodeitUserDetails searchKey = new DiscodeitUserDetails(username);

    List<SessionInformation> sessions = sessionRegistry.getAllSessions(searchKey, false);
    for (SessionInformation session : sessions) {
      session.expireNow();
    }
  }

  @Override
  public boolean isUserOnline(UserDto targetUser) {
    DiscodeitUserDetails searchKey = new DiscodeitUserDetails(targetUser.username());
    List<SessionInformation> sessions = sessionRegistry.getAllSessions(searchKey, false);
    return !sessions.isEmpty();
  }

}
