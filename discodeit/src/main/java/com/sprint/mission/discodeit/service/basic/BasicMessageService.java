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
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
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
  private final ApplicationEventPublisher eventPublisher;

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
    eventPublisher.publishEvent(new MessageCreatedEvent(
        createdMessage.getId(),
        channel.getId(),
        channel.getName(),
        author.getId(),
        author.getUsername(),
        createdMessage.getContent()
    ));
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
  public PageResponse<MessageResponse> findAllByChannelId(
      UUID channelId,
      Instant cursor,
      Pageable pageable
  ) {
    channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    Slice<Message> fetched = cursor == null
        ? messageRepository.findByChannelId(channelId, pageable)
        : messageRepository.findByChannelIdBeforeCursor(channelId, cursor, pageable);

    return pageSliceMapper.toPageResponse(
        fetched,
        messageMapper::toResponse,
        MessageResponse::createdAt
    );
  }

  @Transactional
  @Override
  @PreAuthorize("@messageAccessGuard.isAuthor(#p0, authentication)")
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
  @PreAuthorize("@messageAccessGuard.isAuthor(#p0, authentication)")
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

    eventPublisher.publishEvent(new BinaryContentCreatedEvent(createdBinaryContent.getId(), bytes));
    log.info("Attachment metadata created: binaryContentId={}, fileName={}, size={}",
        createdBinaryContent.getId(), fileName, bytes.length);
    return createdBinaryContent;
  }
}
