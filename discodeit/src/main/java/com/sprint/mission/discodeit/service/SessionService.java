package com.sprint.mission.discodeit.service;

import java.util.Set;

public interface SessionService {

  Set<String> getOnlineUsernames();

  boolean isUserOnline(String username);
}
