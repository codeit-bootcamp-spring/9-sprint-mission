package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
// 스프링이 빈으로 인식할 수 있도록 주석을 해제하여 @Service를 활성화합니다.
@Service
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    // 데이터베이스 상태를 변경하는 작업이므로 트랜잭션을 적용합니다.
    @Transactional
    @Override
    public Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests) {
        UUID channelId = messageCreateRequest.channelId();
        UUID authorId = messageCreateRequest.authorId();

        // [수정됨] 단순히 존재 여부만 확인(existsById)하는 것이 아니라, 연관관계 매핑을 위해 실제 엔티티 객체를 DB에서 찾아옵니다.
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NoSuchElementException("Author with id " + authorId + " does not exist"));

        // [수정됨] UUID 리스트가 아닌, 실제 BinaryContent 엔티티 리스트를 반환하도록 수정합니다.
        List<BinaryContent> attachments = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    // 객체를 저장하고, 저장된 '객체 자체'를 스트림 결과로 반환합니다.
                    return binaryContentRepository.save(binaryContent);
                })
                .toList();

        String content = messageCreateRequest.content();

        // [수정됨] UUID가 아닌, 위에서 찾아온 실제 엔티티 객체(channel, author, attachments)를 넣어 메시지를 생성합니다.
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
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    // 데이터 갱신 작업이므로 트랜잭션을 추가합니다.
    @Transactional
    @Override
    public Message update(UUID messageId, MessageUpdateRequest request) {
        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // 더티 체킹을 활용하여 엔티티 상태만 변경합니다.
        message.update(newContent);

        // 트랜잭션 안에서는 자동 저장되므로 save가 필요 없지만 인터페이스 규격을 맞추기 위해 반환합니다.
        return message;
    }

    // 데이터 삭제 작업이므로 트랜잭션을 추가합니다.
    @Transactional
    @Override
    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // [수정됨] getAttachmentIds()가 아닌 getAttachments()를 호출하여 연관된 객체들의 ID를 뽑아 삭제합니다.
        message.getAttachments()
                .forEach(attachment -> binaryContentRepository.deleteById(attachment.getId()));

        messageRepository.deleteById(messageId);
    }
}