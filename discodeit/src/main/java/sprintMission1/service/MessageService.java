package sprintMission1.service;

import sprintMission1.entity.Channel;
import sprintMission1.entity.Message;
import sprintMission1.entity.User;

import java.util.UUID;

public interface MessageService {
    Message create(String content, User owner, Channel channel);
    void read(UUID messageId);
    void readAll();
    void update(UUID id, String content);
    void delete(UUID id);
}
