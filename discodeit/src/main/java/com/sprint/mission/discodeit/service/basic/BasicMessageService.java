package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.message.MessageView;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    private MessageView toView(Message message) {
        return new MessageView(
                message.getId(),
                message.getChannelId(),
                message.getSenderId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getAttachmentIds()
        );
    }

    @Override
    public MessageView create(MessageCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.channelId() == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
        if (request.params() == null) {
            throw new IllegalArgumentException("params must not be null");
        }
        String content = request.params().content();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        UUID channelId = request.channelId();
        UUID senderId = request.userId();

        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        if (!userRepository.existsById(senderId)) {
            throw new NotFoundException("User not found. id=" + senderId);
        }

        List<MessageCreateRequest.AttachmentParams> attachments = request.params().attachments();

        List<UUID> attachmentIds = new ArrayList<>();
        if (attachments != null) {
            for (MessageCreateRequest.AttachmentParams attachment : attachments) {
                if (attachment == null) {
                    continue;
                }
                byte[] bytes = attachment.bytes();
                String contentType = attachment.contentType();
                String filename = attachment.filename();
                if (bytes == null || bytes.length == 0) {
                    throw new IllegalArgumentException("Attachment bytes must not be null or empty");
                }
                if (contentType == null || contentType.isBlank()) {
                    throw new IllegalArgumentException("Attachment contentType must not be blank");
                }
                if (filename == null || filename.isBlank()) {
                    throw new IllegalArgumentException("Attachment filename must not be blank");
                }
                BinaryContent binaryContent = new BinaryContent(UUID.randomUUID(), bytes, contentType, filename);
                BinaryContent savedBinary = binaryContentRepository.save(binaryContent);
                attachmentIds.add(savedBinary.getId());
            }
        }

        Message message = new Message(channelId, senderId, content);
        message.attach(attachmentIds);
        Message saved = messageRepository.save(message);
        return toView(saved);
    }

    @Override
    public MessageView update(MessageUpdateRequest request) {
        if (request == null || request.messageId() == null || request.params() == null) {
            throw new IllegalArgumentException("request.messageId and request.params are required");
        }
        String content = request.params().content();
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content must not be blank");
        }

        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NotFoundException("Message not found. id=" + request.messageId()));

        message.update(content);
        Message saved = messageRepository.save(message);
        return toView(saved);
    }

    @Override
    public MessageView findById(UUID messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("messageId must not be null");
        }
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found. id=" + messageId));
        return toView(message);
    }

    @Override
    public List<MessageView> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId must not be null");
        }
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(this::toView)
                .toList();
    }

    @Override
    public void delete(MessageDeleteRequest request) {
        if (request == null || request.messageId() == null) {
            throw new IllegalArgumentException("messageId must not be null");
        }

        UUID messageId = request.messageId();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found. id=" + messageId));

        // 관련 도메인 삭제: 첨부파일(BinaryContent)
        if (message.getAttachmentIds() != null) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                if (attachmentId != null && binaryContentRepository.existsById(attachmentId)) {
                    binaryContentRepository.delete(attachmentId);
                }
            }
        }

        messageRepository.delete(messageId);
    }

    @Override
    public boolean existsById(UUID messageId) {
        return messageRepository.existsById(messageId);
    }
}
