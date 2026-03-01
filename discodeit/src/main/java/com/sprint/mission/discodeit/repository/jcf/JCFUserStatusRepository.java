package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> storage = new ConcurrentHashMap<>();

  @Override
  public UserStatus save(UserStatus userStatus) {
    storage.put(userStatus.getId(), userStatus);
    return userStatus;
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return storage.values().stream()
        .filter(us -> us.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return storage.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    storage.remove(id);
  }

  @Override
  public void deleteByUserId(UUID userId) {
    storage.values().removeIf(us -> us.getUserId().equals(userId));
  }
}