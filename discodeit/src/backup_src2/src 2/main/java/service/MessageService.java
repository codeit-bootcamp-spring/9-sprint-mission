package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String chat, UUID channelId, UUID authorID);

    Message find(UUID id);

    List<Message> findAll();

    Message update(UUID id, String chat);

    void delete(UUID id);
}
