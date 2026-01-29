package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    private final BinaryContentRepository binaryContentRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;

    // ===== 기존 CRUD (유지) =====
    @Override
    public Message create(String content, UUID channelId, UUID authorId) {
        Message m = new Message(content, channelId, authorId);
        messageRepository.create(m);
        return m;
    }

    @Override
    public Message findById(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public boolean update(UUID id, String content) {
        return messageRepository.update(id, content);
    }

    @Override
    public boolean delete(UUID id) {
        return messageRepository.delete(id);
    }

    // ===== DTO 기능 =====
    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널: " + request.channelId());
        }
        if (userRepository.findById(request.authorId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저: " + request.authorId());
        }

        Message message = new Message(request.content(), request.channelId(), request.authorId());
        messageRepository.create(message);

        if (request.attachments() != null) {
            for (BinaryContentCreateRequest a : request.attachments()) {
                BinaryContent bc = new BinaryContent(a.filename(), a.contentType(), a.bytes());
                binaryContentRepository.create(bc);
                messageAttachmentRepository.link(message.getId(), bc.getId());
            }
        }

        Message saved = messageRepository.findById(message.getId());
        return toResponse(saved);
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.id());
        if (message == null) return null;

        // 메시지 내용 수정 (엔티티 update가 있으면 사용, 없으면 repository update로만 처리)
        try {
            message.update(request.content());
        } catch (Exception ignored) {
            // 엔티티에 update() 없을 수도 있으니 무시
        }
        messageRepository.update(message.getId(), request.content());

        if (request.attachmentsToAdd() != null) {
            for (BinaryContentCreateRequest a : request.attachmentsToAdd()) {
                BinaryContent bc = new BinaryContent(a.filename(), a.contentType(), a.bytes());
                binaryContentRepository.create(bc);
                messageAttachmentRepository.link(message.getId(), bc.getId());
            }
        }

        Message saved = messageRepository.findById(message.getId());
        return toResponse(saved);
    }

    @Override
    public boolean deleteDto(UUID messageId) {
        Message message = messageRepository.findById(messageId);
        if (message == null) return false;

        List<UUID> attachmentIds =
                messageAttachmentRepository.findAllAttachmentIdsByMessageId(messageId);

        messageAttachmentRepository.deleteAllByMessageId(messageId);

        for (UUID bid : attachmentIds) {
            binaryContentRepository.delete(bid);
        }

        return messageRepository.delete(messageId);
    }

    @Override
    public MessageResponse findDtoById(UUID messageId) {
        Message m = messageRepository.findById(messageId);
        return (m == null) ? null : toResponse(m);
    }

    @Override
    public List<MessageResponse> findAllDtoByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        if (messages == null) return List.of();

        messages.sort(Comparator.comparing(Message::getCreatedAt));

        List<MessageResponse> result = new ArrayList<>();
        for (Message m : messages) {
            result.add(toResponse(m));
        }
        return result;
    }

    private MessageResponse toResponse(Message m) {
        List<UUID> attachmentIds =
                messageAttachmentRepository.findAllAttachmentIdsByMessageId(m.getId());

        return new MessageResponse(
                m.getId(),
                m.getContent(),
                m.getChannelId(),
                m.getAuthorId(),
                attachmentIds,
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }
}

