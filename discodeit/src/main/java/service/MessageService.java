package service;

import entity.*;

import java.util.*;

public interface MessageService {
    Message Create(User writer, Channel channel, String content);

    boolean Remove(UUID id);

    Message findByID(UUID id);

    List<Message> getAll();

    void modifyContent(UUID id, String newContent);

}
