package service;

import entity.Channel;
import entity.Message;
import entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message Create(User writer, Channel channel, String content);

    void Remove(UUID id);

    Message findByID(UUID id);

    List<Message> getAll();

    void modifyContent(UUID id, String newContent);

}
