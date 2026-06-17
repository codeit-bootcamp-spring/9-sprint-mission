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
import com.sprint.mission.discodeit.entity.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper mapper;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패 - 존재하지 않는 채널 ID: {}", messageCreateRequest.channelId());
          return new ChannelNotFoundException(messageCreateRequest.channelId());
        });
    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패 - 존재하지 않는 유저 Id: {}", messageCreateRequest.authorId());
          return new UserNotFoundException(messageCreateRequest.authorId());
        });
    String content = messageCreateRequest.content();
    Message message = new Message(content, channel, author);

    if (binaryContentCreateRequests != null) {
      binaryContentCreateRequests.stream()
          .map(binary -> {
            byte[] bytes = binary.bytes();
            BinaryContent binaryContent = BinaryContent.builder().contentType(binary.contentType())
                .fileName(binary.fileName()).size((long) bytes.length).build();
            binaryContentRepository.save(binaryContent);
            message.getAttachments().add(binaryContent);

            eventPublisher.publishEvent(
                new BinaryContentCreatedEvent(binaryContent.getId(), bytes));
            log.info("파일 업로드 성공 - 파일명: {}, 크기: {} bytes, 파일ID: {}",
                binaryContent.getFileName(), binaryContent.getSize(), binaryContent.getId());
            return binaryContent;
          })
          .toList();


    }

    Message savedMessage = messageRepository.save(message);
    List<UUID> attachmentIds = savedMessage.getAttachments().stream()
        .map(BinaryContent::getId)
        .toList();
    eventPublisher.publishEvent(new MessageCreatedEvent(
        savedMessage.getChannel().getId(),
        savedMessage.getAuthor().getId(),
        savedMessage.getContent(),
        attachmentIds
    ));
    log.info("메시지 생성 완료! - 채널 Id:{}, 유저Id:{},메시지 내용:{},첨부 파일 수:{}",
        channel.getId(), author.getId(), message.getContent(),
        message.getAttachments() != null ? message.getAttachments().size() : 0);

    return mapper.toDto(savedMessage);
  }

  @Override
  @Transactional(readOnly = true)
  public MessageDto find(UUID messageId) {
    return mapper.toDto(messageRepository.findById(messageId)
        .orElseThrow(
            () -> new MessageNotFoundException(messageId)));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    if (!channelRepository.existsById(channelId)) {
      log.warn("채널 Id으로 메시지 조회 실패: 잘못된 채널Id:" + channelId);
      throw new ChannelNotFoundException(channelId);
    }
    Slice<Message> messageSlice = messageRepository.findByChannelIdAndCreatedAtBefore(channelId,
        cursor, pageable);
    Slice<MessageDto> dtoSlice = messageSlice.map(mapper::toDto);
    return PageResponseMapper.fromSlice(dtoSlice, MessageDto::id);

  }

  @Override
  @Transactional
  @PreAuthorize("@messageGuard.isOwner(#messageId,principal.id) or hasRole('ADMIN')")
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> {
              log.warn("메시지 업데이트 실패 - 존재하지 않는 메시지 Id: {}", messageId);
              return new MessageNotFoundException(messageId);
            });
    message.update(request.newContent());
    log.info("메시지 수정 성공- 수정된 메시지 Id: {}, 수정된 메시지 내용: {}", messageId, request.newContent());
    return mapper.toDto(message);
  }

  @Override
  @Transactional
  @PreAuthorize("@messageGuard.isOwner(#messageId,principal.id) or hasRole('ADMIN')")
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> {
              log.warn("메시지 삭제 실패- 존재하지 않는 메시지Id : {}", messageId);
              return new MessageNotFoundException(messageId);
            });

    messageRepository.delete(message);
    log.info("메시지 삭제 성공 - 삭제된 메시지 Id: {} ", messageId);
  }

}
