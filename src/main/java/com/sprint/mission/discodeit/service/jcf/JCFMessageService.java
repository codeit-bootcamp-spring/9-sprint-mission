package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Profile("jcf")
@RequiredArgsConstructor
public class JCFMessageService implements MessageService {

    private final JCFUserRepository userRepository;
    private final JCFChannelRepository channelRepository;
    private final JCFMessageRepository messageRepository;

    @Override
    public MessageResponse create(CreateMessageRequest request) {

        channelRepository.findById(request.channelId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        userRepository.findById(request.senderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        Message message = new Message(
                request.channelId(),
                request.senderId(),
                request.content()
        );

        return MessageResponse.from(
                messageRepository.save(message)
        );
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));

        return messageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(UUID messageId, UpdateMessageRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        message.updateContent(request.content());

        return MessageResponse.from(
                messageRepository.update(message)
        );
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));

        messageRepository.delete(messageId);
    }
}
