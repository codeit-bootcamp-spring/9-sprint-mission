package com.sprint.mission.mission2.repository.jcf;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.entity.User;
import com.sprint.mission.mission2.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message read(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void save(Message message) {
        messages.put(message.getId(), message);
    }

    @Override
    public void remove(UUID id) {
        messages.remove(id);
    }
}
