package main.java.com.sprint.mission.discodeit.service;

import main.java.com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String msgText, UUID senderId, UUID channelId);

    Message findId(UUID id);

    List<Message> findAll();

    Message update(UUID id, String msgText);

    void delete(UUID id);

}