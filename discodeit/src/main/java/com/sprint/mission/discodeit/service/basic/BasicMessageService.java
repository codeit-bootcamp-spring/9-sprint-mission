package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message send(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests) {
    List<UUID> attachmentIds = attachmentRequests.stream()
        .map(att -> {
          BinaryContent bc = new BinaryContent(att.bytes(), att.contentType(), att.fileName(),
              att.size());
          return binaryContentRepository.save(bc).getId();
        })
        .toList();

    Message message = new Message(
        request.content(),
        request.authorId(),
        request.channelId(),
        attachmentIds
    );

    return messageRepository.save(message);
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found"));
    message.update(request.newContent());
    message.recordUpdate();
    return messageRepository.save(message);
  }

  @Override
  public boolean delete(UUID messageId) {
    return messageRepository.findById(messageId).map(message -> {
      if (message.getAttachmentIds() != null) {
        message.getAttachmentIds().forEach(binaryContentRepository::deleteById);
      }
      messageRepository.deleteById(messageId);
      return true;
    }).orElse(false);
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return messageRepository.findById(id);
  }
}