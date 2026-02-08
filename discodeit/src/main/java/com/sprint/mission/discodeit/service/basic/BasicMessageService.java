package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageDTO;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
//    private final ReadStatusRepository readstatusRepositoty;

//    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
//        this.messageRepository = messageRepository;
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//    }



    @Override
    public Message createMessage(MessageDTO.CreateMessageDTO dto){
        Message message = new Message(
                dto.authorId(),
                dto.channelId(),
                dto.text(),
                dto.attachmentIds());

        messageRepository.save(message);
        return message;
    }



    @Override
    public Message findByMessageId(UUID messageId) {
        return messageRepository.findByMessageId(messageId)
                .orElseThrow(() -> new NoSuchElementException
                        ("Message with id " + messageId + "not found"));
    }

    public List<Message> findMessagesBySender(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 사용자");
        }
        return messageRepository.findBySenderId(userId);
    }

    @Override
    public Message findByChannelId(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new IllegalArgumentException("존재하지 않는 채널");
        }
        return (Message) messageRepository.findByChannelId(channelId);
    }

    @Override
    public List<Message> findAllMessage() {
        return messageRepository.findAllMessage();
    }

    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID messageId, String text) {
//        Message message = messageRepository.findByMessageId(messageId)
//                .orElseThrow(() -> new NoSuchElementException
//                        ("Message with id " + messageId + "not found"));
//        return messageRepository.save(message);
        Message message = messageRepository.findAllMessage().stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + "not found"));

//        return new Message(
//                message.getMessageText()
//        );
        return messageRepository.save(message);
    }

    @Override
    public boolean deleteMessage(UUID messageId) {
//        if (messageRepository.existsById(messageId)) {
//            throw new NoSuchElementException
//                    ("Message with id " + messageId + "not found");
//        }
//        messageRepository.deleteById(messageId);
//        return true;
//    }
        Optional<Message> optionalMessage = messageRepository.findByMessageId(messageId);
        if (optionalMessage.isEmpty()) {
            return false;
        }

        Message message = optionalMessage.get();

        if (message.getAttachmentIds().size() != 0) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(attachmentId);
            }
        }
        messageRepository.deleteById(messageId);
        return true;
    }
}
