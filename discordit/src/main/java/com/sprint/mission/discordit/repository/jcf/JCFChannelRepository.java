package com.sprint.mission.discordit.repository.jcf;

import com.sprint.mission.discordit.entity.Channel;
import com.sprint.mission.discordit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFChannelRepository implements ChannelRepository {

  private final Map<UUID, Channel> data;

  public JCFChannelRepository() {
    this.data = new HashMap<>();
  }

  @Override
  public Channel save(Channel channel) {
    this.data.put(channel.getId(), channel);
    return channel;
  }

  @Override
  public Optional<Channel> findById(UUID id) {
    return Optional.ofNullable(this.data.get(id));
  }

  @Override
  public List<Channel> findAll() {
    return this.data.values().stream().toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return this.data.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    this.data.remove(id);
  }
}
