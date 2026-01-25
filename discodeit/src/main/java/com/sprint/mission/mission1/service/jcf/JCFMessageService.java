package com.sprint.mission.mission1.service.jcf;

import com.sprint.mission.mission1.entity.Message;
import com.sprint.mission.mission1.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages = new HashMap<>();

    @Override
    public Message create(UUID channelId, UUID userId, String content) {
        UUID id = UUID.randomUUID();
        Message message = new Message(id, channelId, userId, content);
        messages.put(id, message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        return messages.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public void update(UUID id, String content) {
        Message message = messages.get(id);
        if (message != null) {
            message.update(content);
        } else {
            System.out.println("메세지가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = messages.get(id);
        if (message != null) {
            messages.remove(id);
        } else {
            System.out.println("메세지가 존재하지 않습니다");
        }
    }
}