package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageSliceMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final PageSliceMapper pageSliceMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public MessageResponse create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();
    log.debug(
        "Create message requested: channelId={}, authorId={}, content={}, attachmentsCount={}",
        channelId, authorId, messageCreateRequest.content(), binaryContentCreateRequests.size());

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("authorId", authorId)));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(this::saveBinaryContent)
        .toList();

    Message message = new Message(messageCreateRequest.content(), channel, author);
    attachments.forEach(message::addAttachment);

    Message createdMessage = messageRepository.save(message);
    log.info("Message created: messageId={}, channelId={}, authorId={}",
        createdMessage.getId(), channelId, authorId);
    return messageMapper.toResponse(createdMessage);
  }

  @Override
  public MessageResponse find(UUID messageId) {
    Message message = messageRepository.findByIdWithDetails(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    return messageMapper.toResponse(message);
  }

  @Override
  public PageResponse<MessageResponse> findAllByChannelId(UUID channelId, String cursor, int size) {
    channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    CursorValue cursorValue = parseCursor(cursor);
    int requestedSize = Math.max(1, size);
    PageRequest pageRequest = PageRequest.of(0, requestedSize);

    Slice<Message> fetched = messageRepository.findByChannelIdWithCursor(
        channelId,
        cursorValue == null ? null : cursorValue.createdAt(),
        cursorValue == null ? null : cursorValue.id(),
        pageRequest
    );

    return pageSliceMapper.toPageResponse(fetched, messageMapper::toResponse, this::toCursor);
  }

  private String toCursor(MessageResponse message) {
    return message.createdAt().toString() + "|" + message.id();
  }

  private CursorValue parseCursor(String cursor) {
    if (cursor == null || cursor.isBlank()) {
      return null;
    }

    String[] parts = cursor.split("\\|", 2);
    if (parts.length != 2) {
      throw new DiscodeitException(ErrorCode.INVALID_REQUEST, Map.of("cursor", cursor));
    }

    try {
      Instant createdAt = Instant.parse(parts[0]);
      UUID id = UUID.fromString(parts[1]);
      return new CursorValue(createdAt, id);
    } catch (RuntimeException ex) {
      throw new DiscodeitException(ErrorCode.INVALID_REQUEST, Map.of("cursor", cursor));
    }
  }

  private record CursorValue(Instant createdAt, UUID id) {

  }

  @Transactional
  @Override
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    String content = request.newContent();
    log.debug("Update message requested: messageId={}, newContent={}", messageId, content);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));

    message.update(content);
    log.info("Message updated: messageId={}", messageId);
    return messageMapper.toResponse(message);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    log.debug("Delete message requested: messageId={}", messageId);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));

    messageRepository.delete(message);
    log.info("Message deleted: messageId={}", messageId);
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    String contentType = request.contentType();
    byte[] bytes = request.bytes();
    log.debug("Saving binary content: fileName={}, contentType={}, size={}",
        fileName, contentType, bytes.length);

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);

    try {
      binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    } catch (RuntimeException ex) {
      log.error("Attachment upload failed: binaryContentId={}, fileName={}, contentType={}",
          createdBinaryContent.getId(), fileName, contentType, ex);
      throw ex;
    }
    log.info("Attachment uploaded: binaryContentId={}, fileName={}, size={}",
        createdBinaryContent.getId(), fileName, bytes.length);
    return createdBinaryContent;
  }
}
