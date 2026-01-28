package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public Optional<Message> save(Message message) {
        // 메시지는 보통 즉시 저장되지만, 형식을 맞추기 위해 Optional로 감쌉니다.
        messageRepository.save(message);
        return Optional.of(message);
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