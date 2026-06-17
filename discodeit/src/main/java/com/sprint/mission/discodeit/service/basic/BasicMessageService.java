package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorDetail;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final PageResponseMapper pageResponseMapper;
  private final MessageMapper messageMapper;

  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    log.info("메시지 생성 로직 시작 - 채널 ID: {}, 작성자 ID: {}", messageCreateRequest.channelId(),
        messageCreateRequest.authorId());

    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패: 존재하지 않는 채널 ID 입니다. ({})", messageCreateRequest.channelId());
          return new ChannelException(
              ErrorCode.CHANNEL_NOT_FOUND, messageCreateRequest.channelId().toString());
        });

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패: 존재하지 않는 작성자 ID 입니다. ({})", messageCreateRequest.authorId());
          return new UserException(ErrorCode.USER_NOT_FOUND,
              List.of(new ErrorDetail("authorId", messageCreateRequest.authorId().toString())));
        });

    Message message = new Message(messageCreateRequest.content(), channel, author);

    binaryContentCreateRequests.forEach(request -> {
      byte[] bytes = request.bytes();
      BinaryContent binaryContent = new BinaryContent(
          request.fileName(),
          (long) bytes.length,
          request.contentType()
      );
      BinaryContent savedContent = binaryContentRepository.save(binaryContent);
      eventPublisher.publishEvent(
          new BinaryContentCreatedEvent(savedContent.getId(), bytes)
      );
      message.getAttachments().add(savedContent);
    });

    Message savedMessage = messageRepository.save(message);
    eventPublisher.publishEvent(new MessageCreatedEvent(messageMapper.toDto(savedMessage)));
    log.info("메시지 생성 및 파일 처리 완료!");
    return messageMapper.toDto(savedMessage);
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageMapper.toDto(messageRepository.findById(messageId)
        .orElseThrow(() ->
            new MessageException(ErrorCode.MESSAGE_NOT_FOUND, messageId.toString())
        ));
  }

  @Override
  public List<MessageDto> findAllByChannelId(UUID channelId) {
    return findAllByChannelId(channelId, 0).getContent();
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page) {
    Pageable pageable = PageRequest.of(page, 50, Sort.by("createdAt").descending());
    Slice<Message> messageSlice = messageRepository.findAllByChannel_Id(channelId, pageable);
    return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toDto));
  }

  @PreAuthorize("@messageSecurity.isAuthor(#messageId, principal.userDto.id)")
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.info("메시지 수정 로직 시작 - 대상 메시지 ID: {}", messageId);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("메시지 수정 실패: 존재하지 않는 메시지 ID 입니다. ({})", messageId);
          return new MessageException(ErrorCode.MESSAGE_NOT_FOUND, messageId.toString());
        });
    message.update(request.newContent());
    log.info("메시지 수정 완료 - 대상 메시지 ID: {}", messageId);
    return messageMapper.toDto(messageRepository.save(message));
  }

  @PreAuthorize("@messageSecurity.isAuthor(#messageId, principal.userDto.id)")
  @Override
  public void delete(UUID messageId) {
    log.info("메시지 삭제 로직 시작 - 대상 메시지 ID: {}", messageId);
    messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("메시지 삭제 실패: 존재하지 않는 메시지 ID 입니다. ({})", messageId);
          return new MessageException(ErrorCode.MESSAGE_NOT_FOUND, messageId.toString());
        });
    messageRepository.deleteById(messageId);
    log.info("메시지 삭제 완료 - 대상 메시지 ID: {}", messageId);
  }
}