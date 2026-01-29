package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Message create(UUID channelId, UUID senderId, String content) {
        if (channelId == null) throw new IllegalArgumentException("channelId must not be null");
        if (senderId == null) throw new IllegalArgumentException("senderId must not be null");
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        if (!userRepository.existsById(senderId)) {
            throw new NotFoundException("User not found. id=" + senderId);
        }

        Message message = new Message(channelId, senderId, content);
        return messageRepository.save(message);
    }

    @Override
    public Message update(UUID messageId, String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found. id=" + messageId));

        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

//   채널별 조회
     public List<Message> findAllByChannelId(UUID channelId) {
         return messageRepository.findAllByChannelId(channelId);
     }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NotFoundException("Message not found. id=" + messageId);
        }
        messageRepository.deleteById(messageId);
    }

    @Override
    public boolean existsById(UUID messageId) {
        return messageRepository.existsById(messageId);
    }
}
