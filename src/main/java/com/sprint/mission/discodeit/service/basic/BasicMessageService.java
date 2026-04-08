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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메시지 서비스 구현체.
 * 메시지 CRUD 및 첨부파일 관리를 담당한다.
 * 조회는 커서 기반 페이지네이션(createdAt DESC)을 사용한다.
 */
@Slf4j
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
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    log.info("메시지 생성 요청: channelId={}, authorId={}", channelId, authorId);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    User author = userRepository.findById(authorId)
        .orElseThrow(
            () -> new UserNotFoundException(Map.of("authorId", authorId)));

    List<BinaryContent> attachments = binaryContentCreateRequests.stream()
        .map(req -> {
          BinaryContent bc = new BinaryContent(req.fileName(), (long) req.bytes().length,
              req.contentType());
          bc = binaryContentRepository.save(bc);
          binaryContentStorage.put(bc.getId(), req.bytes());
          return bc;
        })
        .toList();

    Message message = new Message(messageCreateRequest.content(), channel, author, attachments);
    message = messageRepository.save(message);

    log.debug("메시지 생성 완료: messageId={}", message.getId());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    int pageSize = size > 0 ? size : 50;
    PageRequest pageRequest = PageRequest.of(0, pageSize);

    Slice<Message> slice;
    if (cursor != null) {
      slice = messageRepository.findAllByChannelIdAndCreatedAtBefore(channelId, cursor,
          pageRequest);
    } else {
      slice = messageRepository.findAllByChannelId(channelId, pageRequest);
    }

    Slice<MessageDto> dtoSlice = slice.map(messageMapper::toDto);
    return pageResponseMapper.fromSlice(dtoSlice,
        dto -> dto.createdAt().toString());
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new MessageNotFoundException(Map.of("messageId", messageId)));

    log.info("메시지 수정 요청: messageId={}", messageId);
    message.update(request.newContent());
    log.debug("메시지 수정 완료: messageId={}", messageId);
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(
            () -> new MessageNotFoundException(Map.of("messageId", messageId)));

    List<UUID> attachmentIds = message.getAttachments().stream()
        .map(BinaryContent::getId)
        .toList();

    messageRepository.delete(message);

    attachmentIds.forEach(id -> {
      binaryContentStorage.delete(id);
      binaryContentRepository.deleteById(id);
    });
    log.info("메시지 삭제 완료: messageId={}", messageId);
  }
}
