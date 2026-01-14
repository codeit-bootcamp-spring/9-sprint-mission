package service.jcf;

import entity.Message;
import entity.User;
import service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data;
    public JCFMessageService(){
        this.data = new HashMap<>();
    }

    @Override
    public Message addMessage(String newMessage, UUID channelId, UUID authorId) {
        Message message = new Message(newMessage, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return null;
    }

    @Override
    public List<Message> getAllUser() {
        return List.of();
    }

    @Override
    public void updateMessage(UUID id, String message) {

    }

    @Override
    public void deleteMessage(UUID id) {

    }
}
