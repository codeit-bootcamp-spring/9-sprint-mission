package repository;

import entity.Message;
import entity.User;

import java.util.List;

public interface MessageRepository {
    void sendMessage(Message message);
    List<Message> getMessages();
    List<Message> getSenderMessages(User sender);
    List<Message> getReceiverMessages(User receiver);
    boolean deleteMessage(String message,User receiver);


}
