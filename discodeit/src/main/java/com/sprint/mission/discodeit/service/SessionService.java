package com.sprint.mission.discodeit.service;

import java.util.Set;
import java.util.UUID;

public interface SessionService {

  Set<UUID> getOnlineUserIds();

  boolean isUserOnline(UUID uuid);
}
