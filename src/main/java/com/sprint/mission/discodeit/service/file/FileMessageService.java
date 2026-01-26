package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public FileMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        Message message = new Message(content, userId, channelId);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID id) {
        return messageRepository.find(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = messageRepository.find(id);
        if (message != null) {
            message.update(content);
        }
        return message;
    }

    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }
}
