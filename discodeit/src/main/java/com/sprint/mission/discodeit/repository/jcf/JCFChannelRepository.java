package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> storage = new ConcurrentHashMap<>();

  @Override
  public Channel save(Channel channel) {
    storage.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public List<Channel> findAll() {
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
}