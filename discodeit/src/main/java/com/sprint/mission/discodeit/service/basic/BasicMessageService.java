package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;

  @Override
  public Message create(MessageCreateRequest request) {

    UUID channelId = request.channelId();
    UUID authorId = request.authorId();

    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }

    if (!userRepository.existsById(authorId)) {
      throw new NoSuchElementException("Author with id " + authorId + " does not exist");
    }

    List<UUID> attachmentIds = request.attachments() != null
        ? request.attachments().stream()
        .map(file -> {
          try {

            BinaryContent binaryContent = new BinaryContent(
                file.getOriginalFilename(),
                (long) file.getBytes().length,
                file.getContentType(),
                file.getBytes()
            );

            BinaryContent saved =
                binaryContentRepository.save(binaryContent);

            return saved.getId();

          } catch (Exception e) {
            throw new RuntimeException("파일 처리 실패", e);
          }
        })
        .toList()
        : List.of();

    Message message = new Message(
        request.content(),
        channelId,
        authorId,
        attachmentIds
    );

    return messageRepository.save(message);
  }


  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannelId(channelId).stream()
        .toList();
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));
    message.update(newContent);
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NoSuchElementException("Message with id " + messageId + " not found"));

    message.getAttachmentIds()
        .forEach(binaryContentRepository::deleteById);

    messageRepository.deleteById(messageId);
  }
}
