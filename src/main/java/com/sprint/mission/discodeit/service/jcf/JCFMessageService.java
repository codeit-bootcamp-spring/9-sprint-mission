package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
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

    private final JCFUserRepository jcfUserRepository;
    private final JCFChannelRepository jcfChannelRepository;
    private final JCFMessageRepository jcfMessageRepository;

    @Override
    public MessageResponse create(MessageCreateRequest request) {

        if (jcfChannelRepository.findById(request.channelId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (jcfUserRepository.findById(request.senderId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new IllegalArgumentException("메시지 내용은 비어 있을 수 없습니다.");
        }

        Message message = new Message(
                request.channelId(),
                request.senderId(),
                request.content()
        );

        return MessageResponse.from(
                jcfMessageRepository.save(message)
        );
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId는 null일 수 없습니다.");
        }

        if (jcfChannelRepository.findById(channelId) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        return jcfMessageRepository.findAllByChannelId(channelId).stream()
                .map(MessageResponse::from)
                .toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        Message message = jcfMessageRepository.findById(request.messageId());
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        message.updateContent(request.content());

        return MessageResponse.from(
                jcfMessageRepository.update(message)
        );
    }

    @Override
    public void delete(UUID messageId) {
        Message message = jcfMessageRepository.findById(messageId);
        if (message == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        jcfMessageRepository.delete(messageId);
    }
}
