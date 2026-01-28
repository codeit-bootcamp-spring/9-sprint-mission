package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageDto.Response send(MessageDto.CreateRequest request) {
        Message message = new Message(request.content(), request.authorId(), request.channelId());
        if (request.attachmentIds() != null) {
            List<UUID> attachments = new ArrayList<>();
            for (UUID id : request.attachmentIds()) {
                attachments.add(id);
            }
            message.setAttachmentIds(attachments);
        }
        messageRepository.save(message);
        return convertToResponse(message);
    }

    @Override
    public List<MessageDto.Response> findAllByChannelId(UUID channelId) {
        List<Message> messages = messageRepository.findByChannelId(channelId);
        List<MessageDto.Response> responses = new ArrayList<>();
        for (Message msg : messages) {
            responses.add(convertToResponse(msg));
        }
        return responses;
    }

    @Override
    public Optional<MessageDto.Response> findById(UUID id) {
        return messageRepository.findById(id).map(this::convertToResponse);
    }

    @Override
    public boolean delete(UUID id) {
        Optional<Message> msgOpt = messageRepository.findById(id);
        if (msgOpt.isPresent()) {
            for (UUID fileId : msgOpt.get().getAttachmentIds()) {
                binaryContentRepository.delete(fileId);
            }
            messageRepository.delete(id);
            return true;
        }
        return false;
    }

    private MessageDto.Response convertToResponse(Message message) {
        return new MessageDto.Response(message.getId(), message.getContent(), message.getAuthorId(),
                message.getChannelId(), message.getAttachmentIds(), message.getCreatedAt(), message.getUpdatedAt());
    }
}