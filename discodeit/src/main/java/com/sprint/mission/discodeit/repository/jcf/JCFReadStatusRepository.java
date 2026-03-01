package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> storage = new ConcurrentHashMap<>();

  @Override
  public ReadStatus save(ReadStatus readStatus) {
    storage.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
    return storage.values().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .findFirst();
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return storage.values().stream().filter(rs -> rs.getUserId().equals(userId)).toList();
  }

  @Override
  public List<ReadStatus> findAllByChannelId(UUID channelId) {
    return storage.values().stream().filter(rs -> rs.getChannelId().equals(channelId)).toList();
  }

  @Override
  public Optional<ReadStatus> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
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
  public void deleteAllByChannelId(UUID channelId) {
    storage.values().removeIf(rs -> rs.getChannelId().equals(channelId));
  }
}