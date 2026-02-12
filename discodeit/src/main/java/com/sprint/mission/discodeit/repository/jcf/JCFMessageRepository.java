package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageMap = new HashMap<>();

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
        List<Message> result = new ArrayList<>();
        for (Message message : messageMap.values()) {
            if (message.getChannelId().equals(channelId)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public Optional<Message> findLatestByChannelId(UUID channelId) {
        Message latest = null;
        for (Message message : messageMap.values()) {
            if (message.getChannelId().equals(channelId)) {
                if (latest == null || message.getCreatedAt().isAfter(latest.getCreatedAt())) {
                    latest = message;
                }
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public void delete(UUID id) {
        messageMap.remove(id);
    }
}