package service;

import entity.Message;
import entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    void sendMessage(Message message);
    List<Message> getMessages();
    List<Message> getSenderMessages(User sender);
    List<Message> getReceiverMessages(User receiver);
    boolean deleteMessage(String message,User receiver);

}
