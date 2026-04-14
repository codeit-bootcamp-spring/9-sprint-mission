package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  //
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments) {
    log.debug("메시지 생성 시작: {}", messageCreateRequest);
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

    Channel channel = channelRepository.findById(channelId).orElseThrow(() -> {
      log.warn("존재하지 않는 채널: {}", channelId);
      return new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND,
          Map.of("channelId", channelId));
    });
    User author = userRepository.findById(authorId).orElseThrow(() -> {
      log.warn("존재하지 않는 사용자: {}", authorId);
      return new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", authorId));
    });

    String content = messageCreateRequest.content();
    List<BinaryContent> binaryContents = new ArrayList<>();

    if (attachments != null && !attachments.isEmpty()) {
      log.debug("메시지 내 첨부파일 처리 시작: {}", attachments.size());
      for (MultipartFile file : attachments) {
        BinaryContent metadata = new BinaryContent(
            file.getOriginalFilename(),
            file.getSize(),
            file.getContentType()
        );

        BinaryContent savedMetadata = binaryContentRepository.save(metadata);
        binaryContents.add(savedMetadata);

        try {
          binaryContentStorage.put(savedMetadata.getId(), file.getBytes());
        } catch (IOException e) {
          log.error("파일 저장 오류: {}", file.getOriginalFilename());
          throw new RuntimeException("Failed file saved: " + file.getOriginalFilename(), e);
        }
      }
    }

    Message message = new Message(
        content,
        channel,
        author,
        binaryContents
    );

    MessageDto dto = this.convertToDto(messageRepository.save(message));
    log.info("메시지 생성 성공: {}", dto);

    return dto;
  }

  @Override
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findById(messageId).orElseThrow(() -> {
      log.warn("조회 실패. 존재하지 않는 메시지ID: {}", messageId);
      return new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND,
          Map.of("messageId", messageId));
    });

    return this.convertToDto(message);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    log.debug("메시지 업데이트 시작: {}", messageId);
    Message message = messageRepository.findById(messageId).orElseThrow(() -> {
      log.warn("업데이트 실패. 존재하지 않는 메시지ID: {}", messageId);
      return new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND,
          Map.of("messageId", messageId));
    });

    message.update(request.newContent());

    MessageDto dto = this.convertToDto(message);
    log.info("message 수정 완료");

    return dto;
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId).orElseThrow(() -> {
      log.warn("존재하지 않는 메시지ID: {}", messageId);
      return new MessageNotFoundException(ErrorCode.MESSAGE_NOT_FOUND,
          Map.of("messageId", messageId));
    });

    messageRepository.delete(message);
    log.info("메시지 삭제 완료: {}", messageId);
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable) {
    Slice<Message> messages = messageRepository.findOlderByChannelId(channelId, cursor, pageable);

    Slice<MessageDto> dtoPage = messages.map(this::convertToDto);

    Instant nextCursor = null;
    if (messages.hasNext() && !messages.getContent().isEmpty()) {
      nextCursor = messages.getContent().get(messages.getContent().size() - 1).getCreatedAt();
    }

    return pageResponseMapper.fromSlice(dtoPage, nextCursor);
  }

  private MessageDto convertToDto(Message message) {
    List<BinaryContentDto> binaryContentDtos = List.of();
    if (message.getAttachments() != null && !message.getAttachments().isEmpty()) {
      binaryContentDtos = message.getAttachments().stream()
          .map(binaryContentMapper::toDto)
          .toList();
    }
    return messageMapper.toDto(message, binaryContentDtos);
  }
}
