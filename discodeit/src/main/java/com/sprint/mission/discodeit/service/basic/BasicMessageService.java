package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PageResponseMapper pageResponseMapper;

  @Override
  public Message create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    log.info("메시지 생성 로직 시작 - 채널 ID: {}, 작성자 ID: {}", messageCreateRequest.channelId(),
        messageCreateRequest.authorId());

    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패: 존재하지 않는 채널 ID 입니다. ({})", messageCreateRequest.channelId());
          return new ChannelException(
              ErrorCode.CHANNEL_NOT_FOUND, Map.of("channelId", messageCreateRequest.channelId()));
        });

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> {
          log.warn("메시지 생성 실패: 존재하지 않는 작성자 ID 입니다. ({})", messageCreateRequest.authorId());
          return new UserException(ErrorCode.USER_NOT_FOUND,
              Map.of("authorId", messageCreateRequest.authorId()));
        });

    // 메시지 객체 먼저 생성
    Message message = new Message(messageCreateRequest.content(), channel, author);

    // 첨부파일 처리 및 메시지에 연결
    binaryContentCreateRequests.forEach(request -> {
      byte[] bytes = request.bytes();
      BinaryContent binaryContent = new BinaryContent(
          request.fileName(),
          (long) bytes.length,
          request.contentType()
      );

      // DB 저장
      BinaryContent savedContent = binaryContentRepository.save(binaryContent);
      // Storage 저장
      binaryContentStorage.put(savedContent.getId(), bytes);

      // 메시지 엔티티의 리스트에 추가
      message.getAttachments().add(savedContent);
    });
    log.info("메시지 생성 및 파일 처리 완료!");
    return messageRepository.save(message);
  }

  @Override
  public Message find(UUID messageId) {
    return messageRepository.findById(messageId)
        .orElseThrow(() ->
            // 메시지를 찾을 수 없으므로 MessageException 객체 생성 및 반환
            new MessageException(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId))
        );
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return findAllByChannelId(channelId, 0).getContent();
  }

  @Override
  public PageResponse<Message> findAllByChannelId(UUID channelId, int page) {
    Pageable pageable = PageRequest.of(page, 50, Sort.by("createdAt").descending());

    Slice<Message> messageSlice = messageRepository.findAllByChannel_Id(channelId, pageable);

    return pageResponseMapper.fromSlice(messageSlice);
  }

  @Override
  public Message update(UUID messageId, MessageUpdateRequest request) {
    log.info("메시지 수정 로직 시작 - 대상 메시지 ID: {}", messageId);
    String newContent = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("메시지 수정 실패: 존재하지 않는 메시지 ID 입니다. ({})", messageId);
          return new MessageException(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
        });

    message.update(newContent);
    log.info("메시지 수정 완료 - 대상 메시지 ID: {}", messageId);
    return messageRepository.save(message);
  }

  @Override
  public void delete(UUID messageId) {
    log.info("메시지 삭제 로직 시작 - 대상 메시지 ID: {}", messageId);
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("메시지 삭제 실패: 존재하지 않는 메시지 ID 입니다. ({})", messageId);
          return new MessageException(ErrorCode.MESSAGE_NOT_FOUND, Map.of("messageId", messageId));
        });

    messageRepository.deleteById(messageId);
    log.info("메시지 삭제 완료 - 대상 메시지 ID: {}", messageId);
  }
}
