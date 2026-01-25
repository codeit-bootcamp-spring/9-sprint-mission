package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public Message save(Message message) {
        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> findAllByContentKeyword(String keyword) {
        return messageRepository.findAll().stream()
                .filter(m -> m.getContent().contains(keyword))
                .toList();
    }

    @Override
    public Optional<Message> findById(UUID id) { return messageRepository.findById(id); }

    @Override
    public List<Message> findAll() { return messageRepository.findAll(); }

    @Override
    public List<Message> findByChannelId(UUID channelId) { return messageRepository.findByChannelId(channelId); }

    @Override
    public void update(Message message) { messageRepository.save(message); }

    @Override
    public boolean delete(UUID id) {
        if (messageRepository.findById(id).isPresent()) {
            messageRepository.delete(id);
            return true;
        }
        return false;
    }
}