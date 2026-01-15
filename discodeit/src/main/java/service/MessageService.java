package service;

import entity.*;

import java.util.*;

public interface MessageService {
    Message Create(UUID writerId, UUID channelId, String content);

    void Remove(UUID id);

    Message findByID(UUID id);

    List<Message> getAll();

    Message modifyContent(UUID id, String newContent);

}
