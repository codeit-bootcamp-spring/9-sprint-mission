package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
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
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final PageResponseMapper pageResponseMapper;
  private final MessageMapper messageMapper;
  private final ApplicationEventPublisher eventPublisher;


  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    if (!channelRepository.existsById(channelId)) {
      log.warn("채널 검색 실패 - 채널 ID: {}", channelId);
      throw new ChannelNotFoundException(channelId);
    }
    if (!userRepository.existsById(authorId)) {
      log.warn("작성자 검색 실패 - 유저 ID: {}", authorId);
      throw new UserNotFoundException(authorId);
    }
    List<UUID> attachmentIds = new ArrayList<>();
    if (!binaryContentCreateRequests.isEmpty()) {
      attachmentIds = binaryContentCreateRequests.stream()
          .map(attachmentRequest -> {
            String fileName = attachmentRequest.fileName();
            String contentType = attachmentRequest.contentType();
            byte[] bytes = attachmentRequest.bytes();

            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
                contentType);
            BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
            log.debug("메시지 내 파일 저장 시도 - 파일 정보: {}", createdBinaryContent);
            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
            );
            return createdBinaryContent.getId();
          })
          .toList();
    }
    String content = messageCreateRequest.content();
    Channel channel = channelRepository.findById(channelId).orElse(null);
    User author = userRepository.findById(authorId).orElse(null);
    List<BinaryContent> attachments = binaryContentRepository.findAllByIdIn(attachmentIds);
    Message message = new Message(
        content,
        channel,
        author,
        attachments
    );
    Message result = messageRepository.save(message);
    MessageDto messageDto = messageMapper.toDto(result);
    log.info("메시지 저장 완료 - 메시지: {}", result);
    eventPublisher.publishEvent(
        new MessageCreatedEvent(messageDto)
    );
    return messageDto;
  }

  @Transactional(readOnly = true)
  @Override
  public Message find(UUID messageId) {
    return messageRepository.findWithChannelAuthorAttachmentById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
  }

  @Transactional(readOnly = true)
  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    Slice<Message> messageSlice;
    if (cursor == null) {
      messageSlice = messageRepository.findAllWithExtraByChannelId(channelId, pageable);
    } else {
      messageSlice = messageRepository.findAllByCursor(channelId, cursor, pageable);
    }

    return pageResponseMapper.fromSlice(
        messageSlice,
        message -> messageMapper.toDto(message),
        message -> message.getCreatedAt()
    );
  }

  @Transactional
  @Override
  @PreAuthorize("@securityService.isMessageOwner(#messageId) or hasRole('ADMIN')")
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String newContent = request.newContent();
    Message message = messageRepository.findWithChannelAuthorAttachmentById(messageId)
        .orElseThrow(() ->
        {
          log.warn("메시지 검색 실패 - 메시지 ID: {}", messageId);
          return new MessageNotFoundException(messageId);
        });
    message.update(newContent);
    messageRepository.save(message);
    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  @PreAuthorize("@securityService.isMessageOwner(#messageId) or hasRole('ADMIN')")
  public void delete(UUID messageId) {
    if (!messageRepository.existsById(messageId)) {
      log.warn("메시지 검색 실패 - 메시지 ID: {}", messageId);
      throw new MessageNotFoundException(messageId);
    }
    log.info("메시지 삭제 - 메시지 ID: {}", messageId);
    messageRepository.deleteById(messageId);
  }

}
