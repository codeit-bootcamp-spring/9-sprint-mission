package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateRequest req, List<BinaryContentCreateRequest> files) {
        UUID channelId = req.channelId();   // ✅ 컨트롤러가 URL 값으로 덮어준 값
        UUID authorId = req.authorId();

        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist.");
        }
        if (!userRepository.existsById(authorId)) {
            throw new NoSuchElementException("User with id " + authorId + " does not exist.");
        }

        // 첨부가 아직 미구현/단순화 상태면 일단 빈 리스트로
        List<UUID> attachmentIds = Collections.emptyList();

        Message message = new Message(
                req.content(),
                channelId,
                authorId,
                attachmentIds
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " does not exist."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        Message message = find(messageId);
        message.update(request.newContent());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        messageRepository.deleteById(messageId);
    }
}
