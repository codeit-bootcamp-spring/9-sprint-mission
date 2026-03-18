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
import com.sprint.mission.discodeit.repository.jpa.BinaryContentJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.ChannelJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.MessageJpaRepository;
import com.sprint.mission.discodeit.repository.jpa.UserJpaRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageJpaRepository messageRepository;
  private final ChannelJpaRepository channelRepository;
  private final UserJpaRepository userRepository;
  private final BinaryContentService binaryContentService;

  @Override
  public Message create(MessageCreateRequest request) {

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() ->
            new NoSuchElementException("Channel with id " + request.channelId() + " not found"));

    User author = userRepository.findById(request.authorId())
        .orElseThrow(() ->
            new NoSuchElementException("Author with id " + request.authorId() + " not found"));

    List<BinaryContent> attachments = request.attachments() != null
        ? request.attachments().stream()
        .map(file -> createBinaryContent(file))
        .toList()
        : List.of();

    Message message = new Message(
        request.content(),
        channel,
        author,
        attachments
    );

    return messageRepository.save(message);
  }

  private BinaryContent createBinaryContent(MultipartFile file) {
    try {
      BinaryContentCreateRequest createRequest =
          new BinaryContentCreateRequest(
              file.getOriginalFilename(),
              file.getContentType(),
              file.getBytes()
          );

      return binaryContentService.create(createRequest);

    } catch (IOException e) {
      throw new RuntimeException("파일 처리 실패", e);
    }
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

    message.getAttachments()
        .forEach(binaryContent ->
            binaryContentService.delete(binaryContent.getId()));

    messageRepository.delete(message);
  }
}
