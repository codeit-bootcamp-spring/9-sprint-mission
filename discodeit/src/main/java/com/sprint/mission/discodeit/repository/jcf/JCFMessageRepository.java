package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new ConcurrentHashMap<>();

    public JCFMessageRepository() {}

    @Override
    public void save(Message message) {
        messageMap.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageMap.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageMap.values().stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        messageMap.remove(id);
    }
}