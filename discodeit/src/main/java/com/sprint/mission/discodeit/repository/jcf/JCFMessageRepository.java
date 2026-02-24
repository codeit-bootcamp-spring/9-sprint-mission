package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageRepository implements MessageRepository {

  private final Map<UUID, Message> storage = new ConcurrentHashMap<>();

  @Override
  public Message save(Message message) {
    storage.put(message.getId(), message);
    return message;
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return storage.values().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Optional<Message> findLatestByChannelId(UUID channelId) {
    return findAllByChannelId(channelId).stream()
        .max(Comparator.comparing(Message::getCreatedAt));
  }

  @Override
  public Optional<Message> findById(UUID id) {
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
    storage.values().removeIf(m -> m.getChannelId().equals(channelId));
  }
}