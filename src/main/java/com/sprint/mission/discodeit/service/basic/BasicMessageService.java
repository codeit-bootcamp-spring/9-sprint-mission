package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        if (channelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }
        if (userRepository.findById(request.senderId()) == null) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }
        List<UUID> attachmentIds = request.attachmentIds() != null ? request.attachmentIds() : List.of();
        Message m = new Message(request.channelId(), request.senderId(), request.content(), attachmentIds);
        messageRepository.save(m);
        return MessageResponse.from(m);
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelRepository.findById(channelId) == null) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }
        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message m = messageRepository.findById(request.messageId());
        if (m == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }
        m.updateContent(request.content());
        messageRepository.update(m);
        return MessageResponse.from(m);
    }

    @Override
    public void delete(UUID messageId) {
        Message m = messageRepository.findById(messageId);
        if (m == null) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }
        for (UUID attachmentId : m.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }
        messageRepository.delete(messageId);
    }
}
