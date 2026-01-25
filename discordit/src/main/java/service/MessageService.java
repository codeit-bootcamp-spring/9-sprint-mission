package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message addMessage(String newMessage, UUID channelId, UUID authorId);

    Message getMessage(UUID id);

    List<Message> getAllMessage();

    void updateMessage(UUID id, String newMessage);

    void deleteMessage(UUID id);
}
