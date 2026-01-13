package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String chat);

    Message find(UUID id);

    List<Message> findAll();

    Message update(String chat);

    void delete(UUID id);
}
