package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.PageResponse;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests) {

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new NoSuchElementException(
            "Channel with id " + request.channelId() + " not found"));
    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new NoSuchElementException(
            "Author with id " + request.authorId() + " not found"));

    List<BinaryContent> attachments = attachmentRequests.stream()
        .map(req -> {
          BinaryContent binaryContent = new BinaryContent(
              req.fileName(),
              (long) req.bytes().length,
              req.contentType()
          );
          BinaryContent saved = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saved.getId(), req.bytes());
          return saved;
        })
        .toList();

    Message message = new Message(request.content(), channel, author);
    message.addAttachments(attachments);

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor) {
    Pageable pageable = PageRequest.of(0, 50);

    Slice<Message> slice = (cursor == null)
        ? messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable)
        : messageRepository.findByChannelIdBeforeCursor(channelId, cursor, pageable);

    Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);

    Object nextCursor = slice.hasNext()
        ? dtoSlice.getContent().get(dtoSlice.getContent().size() - 1).createdAt()
        : null;

    return pageResponseMapper.fromSlice(dtoSlice, nextCursor);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));
    message.update(request.newContent());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException(
            "Message with id " + messageId + " not found"));

    message.getAttachments().forEach(attachment -> {
      binaryContentRepository.deleteById(attachment.getId());
      binaryContentStorage.delete(attachment.getId());
    });

    messageRepository.deleteById(messageId);
  }
}