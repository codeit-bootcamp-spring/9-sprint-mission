package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binary.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentService binaryContentService;

    @Override
    public Message create(MessageCreateRequestDto dto) {
        if (!channelRepository.existsById(dto.getChannelId())) {
            throw new NoSuchElementException("Channel not found");
        }
        if (!userRepository.existsById(dto.getAuthorId())) {
            throw new NoSuchElementException("User not found");
        }

        List<UUID> attachmentIds = new ArrayList<>();

        if (dto.getAttachments() != null) {
            for (BinaryContentCreateRequestDto attachment : dto.getAttachments()) {
                BinaryContent binaryContent = binaryContentService.create(attachment);
                attachmentIds.add(binaryContent.getId());
            }
        }

        Message message = new Message(
                dto.getContent(),
                dto.getChannelId(),
                dto.getAuthorId(),
                attachmentIds
        );

        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = find(messageId);
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        Message message = find(messageId);

        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentService.delete(attachmentId);
        }

        messageRepository.deleteById(messageId);
    }
}
