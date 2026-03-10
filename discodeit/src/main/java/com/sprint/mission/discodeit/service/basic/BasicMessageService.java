package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 누락되어 있던 스토리지 인터페이스 주입
    private final BinaryContentStorage binaryContentStorage;

    @Transactional
    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    // 메타데이터만 엔티티로 DB 저장
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
                    binaryContentRepository.save(binaryContent);
                    // 실제 파일은 로컬 스토리지에 분리 저장
                    binaryContentStorage.put(binaryContent.getId(), bytes);

                    return binaryContent;
                })
                .toList();

        String content = messageCreateRequest.content();

        Message message = new Message(
                content,
                channel,
                author,
                attachments
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public PageResponse<Message> findAllByChannelId(UUID channelId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messagePage = messageRepository.findAllByChannelId(channelId, pageable);
        return PageResponse.from(messagePage);
    }

    @Transactional
    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.update(newContent);
        return message;
    }

    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        message.getAttachments()
                .forEach(attachment -> binaryContentRepository.deleteById(attachment.getId()));

        messageRepository.deleteById(messageId);
    }
}