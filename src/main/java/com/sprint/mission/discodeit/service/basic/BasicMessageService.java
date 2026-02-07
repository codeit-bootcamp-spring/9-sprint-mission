package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Primary
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    public BasicMessageService(
            MessageRepository messageRepository,
            ChannelRepository channelRepository,
            UserRepository userRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (userRepository.find(request.getUserId()) == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (channelRepository.find(request.getChannelId()) == null) {
            throw new IllegalArgumentException("Channel not found.");
        }

        Message message = new Message(request.getContent(), request.getUserId(), request.getChannelId(), new ArrayList<>());
        messageRepository.save(message);

        if (request.getAttachments() != null) {
            for (BinaryContentCreateRequest attachment : request.getAttachments()) {
                BinaryContent content = new BinaryContent(
                        null,
                        message.getId(),
                        attachment.getFileName(),
                        attachment.getContentType(),
                        attachment.getData()
                );
                BinaryContent saved = binaryContentRepository.save(content);
                message.getAttachmentIds().add(saved.getId());
            }
        }

        return toResponse(message);
    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.find(messageId);
        if (message == null) {
            return null;
        }
        return toResponse(message);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        List<MessageResponse> responses = new ArrayList<>();
        for (Message message : messageRepository.findAllByChannelId(channelId)) {
            responses.add(toResponse(message));
        }
        return responses;
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.find(request.getId());
        if (message == null) {
            throw new IllegalArgumentException("Message not found.");
        }
        message.update(request.getContent());
        return toResponse(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.find(messageId);
        if (message == null) {
            return;
        }
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }
        messageRepository.delete(messageId);
    }

    private MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .userId(message.getUserId())
                .channelId(message.getChannelId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .attachmentIds(new ArrayList<>(message.getAttachmentIds()))
                .build();
    }
}
