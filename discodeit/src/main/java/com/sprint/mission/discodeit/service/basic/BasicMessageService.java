package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageResponse send(MessageCreateRequest request) {
        Message message = new Message(request.content(), request.authorId(), request.channelId());
        if (request.attachmentIds() != null) {
            message.setAttachmentIds(request.attachmentIds());
        }
        messageRepository.save(message);
        return convertToResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId).stream()
                .map(this::convertToResponse).toList();
    }

    @Override
    public Optional<MessageResponse> findById(UUID id) {
        return messageRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public boolean delete(UUID id) {
        return messageRepository.findById(id).map(message -> {
            if (message.getAttachmentIds() != null) {
                message.getAttachmentIds().forEach(binaryContentRepository::delete);
            }
            messageRepository.delete(id);
            return true;
        }).orElse(false);
    }

    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(message.getId(), message.getContent(), message.getAuthorId(),
                message.getChannelId(), message.getAttachmentIds(), message.getCreatedAt(), message.getUpdatedAt());
    }
}