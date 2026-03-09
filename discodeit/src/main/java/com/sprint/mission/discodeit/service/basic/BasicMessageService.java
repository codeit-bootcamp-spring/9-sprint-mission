package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NotFoundException("Channel with id " + channelId + " does not exist"));
    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new NotFoundException("Author with id " + authorId + " does not exist"));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(this::saveBinaryContent)
        .toList();

    Message message = new Message(messageCreateRequest.content(),
        channel, author);
    attachments.forEach(message::addAttachment);

    Message createdMessage = messageRepository.save(message);
    return messageMapper.toDto(createdMessage);
  }

  @Override
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NotFoundException("Message with id " + messageId + " not found"));
    return messageMapper.toDto(message);
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return messageRepository.findAllByChannel_Id(channelId).stream()
        .map(messageMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String content = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NotFoundException("Message with id " + messageId + " not found"));
    message.update(content);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new NotFoundException("Message with id " + messageId + " not found"));

    messageRepository.delete(message);
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    String contentType = request.contentType();
    byte[] bytes = request.bytes();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    return createdBinaryContent;
  }
}
