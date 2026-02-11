package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
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
    public MessageResponse create(CreateMessageRequest request) {

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        userRepository.findById(request.senderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<UUID> attachmentIds =
                request.attachmentIds() != null ? request.attachmentIds() : List.of();

        Message message = new Message(
                request.channelId(),
                request.senderId(),
                request.content(),
                attachmentIds
        );

        messageRepository.save(message);
        return MessageResponse.from(message);
    }


    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {

        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, UpdateMessageRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 메세지입니다."));

        message.updateContent(request.content());
        messageRepository.update(message);
        return MessageResponse.from(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 메세지입니다."));

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.delete(attachmentId);
        }
        messageRepository.delete(messageId);
    }
}
