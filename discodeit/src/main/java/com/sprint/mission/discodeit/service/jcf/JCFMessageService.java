package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageSevice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageSevice {
    private final List<Message>  data = new ArrayList<>();


    @Override
    public Message create(String name, String messages, UUID user) {
        Message message = new Message(name, messages, user);
        data.add(message);
        return message;
    }


    @Override
    public Message find(UUID user) {
        for (Message message : data) {
            if (message.getUser().equals(user)) {
                return message;
            }
        }
        return null;
    }
    @Override
    public List<Message> findAll() {
        return data;
    }

    public Message updateMessage(UUID userUUID, String newMessage) {
        for (Message m : data) {
            Message updated = m.updateMessageIfUser(userUUID, newMessage); // OK
            if (updated != null) {
                return updated;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID userMessage) {
        data.removeIf(m -> m.getUser().equals(userMessage));
    }
}
