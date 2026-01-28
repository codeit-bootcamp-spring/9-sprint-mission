package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> data = new ArrayList<>();

    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        Message message = new Message(content, channelId, authorId);
        data.add(message);
        return message;
    }

    @Override
    public Message find(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = find(id);
        if (message != null) {
            message.setContent(content);
        }
        return message;
    }

    @Override
    public boolean delete(UUID id) {
        return data.removeIf(m -> m.getId().equals(id));
    }
}