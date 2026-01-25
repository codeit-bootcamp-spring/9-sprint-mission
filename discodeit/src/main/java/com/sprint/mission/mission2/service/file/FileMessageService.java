package com.sprint.mission.mission2.service.file;

import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.repository.MessageRepository;
import com.sprint.mission.mission2.repository.file.FileMessageRepository;
import com.sprint.mission.mission2.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    MessageRepository messageRepository = new FileMessageRepository();

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
            messageRepository.save(message);
        }
        else {
            System.out.println("메세지가 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID id) {
        messageRepository.remove(id);
    }
}
