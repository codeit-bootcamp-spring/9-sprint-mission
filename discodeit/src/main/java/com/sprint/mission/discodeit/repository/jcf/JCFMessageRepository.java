package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> store = new HashMap<>();

    @Override
    public Message save(Message message) {
        store.put(message.getMessageId(),message);
        return message;
    }

    @Override
    public Optional<Message> findByMessageId(UUID messageId) {
        return Optional.ofNullable
                (store.get(messageId));
    }

    @Override
    public List<Message> findBySenderId(UUID userId) {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(UUID messageId) {
        store.remove(messageId);
    }

    @Override
    public boolean existsById(UUID messageId) {
        return store.containsKey(messageId);
    }
}
