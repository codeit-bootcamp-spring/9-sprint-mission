package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.DTO.AttachmentCreatRequest;
import com.sprint.mission.discodeit.service.DTO.Message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.DTO.Message.MessageUpdateRequest;
import com.sprint.mission.discodeit.service.DTO.Message.MessageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.status.BinaryContentInterface;
import com.sprint.mission.discodeit.status.adds.BinaryContent;
//import com.sprint.mission.discodeit.status.adds.BinaryContentInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentInterface binaryContentInterface;
    private final RequestAttributes requestAttributes;
    //private MessageRepository binaryContentInterface;


    @Override
    public Message create(MessageCreateRequest request) {
        if (!channelRepository.existsById(request.channelId())) {
            throw new NoSuchElementException("Channel not found with id " + request.channelId());
        }
        if (!userRepository.existsById(request.authorId())) {
            throw new NoSuchElementException("Author not found with id " + request.authorId());
        }

        Message message = new Message(
                request.content(),
                request.channelId(),
                request.authorId(),
                request.attachments()

        );
       messageRepository.save(message);

        if (request.attachments() != null) {
            for (AttachmentCreatRequest attachments : request.attachments()) {
                BinaryContent binaryContent = new BinaryContent(
                        UUID.randomUUID(),
                        request.authorId(),
                        message.getId(),
                        Instant.now()
                );
                binaryContentInterface.save(binaryContent);
            }
        }

        return message;
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("없는 채널");
        }

        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(message -> new MessageResponse(
                        message.getId(),
                        message.getChannelId(),
                        message.getAuthorId(),
                        message.getContent(),
                        message.getCreatedAt()
                ))
                .toList();
    }

    @Override
    public Message update(MessageUpdateRequest request) {
        Message message = messageRepository.findById(request.messageId())
                .orElseThrow(() -> new NoSuchElementException("없는 메세지"));
        message.update(
                request.newContent()
        );
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("없는 메세지");
        }
        binaryContentInterface.deleteByMessageId(messageId);

        messageRepository.deleteById(messageId);
    }
}
