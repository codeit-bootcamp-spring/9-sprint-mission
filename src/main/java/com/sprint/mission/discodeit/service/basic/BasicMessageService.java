package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageRepository messageRepository;

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {
        channelService.findById(channelId);
        userService.findById(senderId);

        Message message = new Message(channelId, senderId, content);
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id);
        if (message == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }
        return message;
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        channelService.findById(channelId);
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {
        userService.findById(senderId);
        return messageRepository.findBySenderId(senderId);
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        message.updateContent(content);
        return messageRepository.update(message);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        messageRepository.delete(id);
    }
}
