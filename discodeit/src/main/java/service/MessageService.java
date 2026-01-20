package service;

import entity.*;

import java.util.*;

public interface MessageService {
    Message create(UUID writerId, UUID channelId, String content);

    void remove(UUID id);

    Message findByID(UUID id);

    List<Message> getAll();

    Message updateContent(UUID id, String newContent);

    List<Message> findByUserID(UUID userId);
}
