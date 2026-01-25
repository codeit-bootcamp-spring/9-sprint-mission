package com.sprint.mission.mission2.service.jcf;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.repository.MessageRepository;
import com.sprint.mission.mission2.repository.jcf.JCFMessageRepository;
import com.sprint.mission.mission2.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    MessageRepository messageRepository = new JCFMessageRepository();

    @Override
    public Message create(UUID channelId, UUID userId, String content) {
        UUID id = UUID.randomUUID();
        Message message = new Message(id, channelId, userId, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        return messageRepository.read(id);
    }

    @Override
    public List<Message> readAll() {
        return messageRepository.readAll();
    }

    @Override
    public void update(UUID id, String content) {
        Message message = messageRepository.read(id);
        if (message != null) {
            message.update(content);
        } else {
            System.out.println("메세지가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = messageRepository.read(id);
        if (message != null) {
            messageRepository.remove(id);
        } else {
            System.out.println("메세지가 존재하지 않습니다");
        }
    }
}