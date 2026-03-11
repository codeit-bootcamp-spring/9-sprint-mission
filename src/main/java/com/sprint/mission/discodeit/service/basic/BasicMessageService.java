package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    //
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto create(MessageCreateRequest request) {

        UUID channelId = request.channelId();
        UUID authorId = request.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("Author not found");
        }

        Message message = new Message(
            request.content(),
            channelId,
            authorId,
            List.of() // attachments는 일단 비워도 됨
        );

        Message saved = messageRepository.save(message);
        return toDto(saved);
    }

    @Override
    public MessageDto find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException("Message not found"));

        return toDto(message);
    }

    @Override
    public List<MessageDto> findByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {

        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalArgumentException("메시지 없음"));

        message.update(request.newContent());  // 🔥 이게 핵심

        Message updated = messageRepository.save(message);

        return toDto(updated);

    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deleteById(messageId);
    }

    private MessageDto toDto(Message message) {
        return new MessageDto(
            message.getId(),
            message.getChannelId(),
            message.getAuthorId(),
            message.getContent(),
            message.getCreatedAt()
        );
    }
}
