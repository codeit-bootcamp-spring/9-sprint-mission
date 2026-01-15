package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.Message;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message createMessage(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(UUID id, Message newMessage) {
        Message foundMessage = data.get(id); // 찾기
        if(foundMessage != null){
            foundMessage.update(newMessage.getContent());
        }
        return foundMessage;
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
