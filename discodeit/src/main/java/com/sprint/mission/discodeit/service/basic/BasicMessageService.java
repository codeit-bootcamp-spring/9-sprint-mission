package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentService binaryContentService;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto send(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests) {
    // 1. 연관 엔티티 조회 (객체 참조 매핑 준수)
    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new NoSuchElementException("작성자를 찾을 수 없습니다."));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

    // 2. 첨부파일 생성 및 엔티티 목록화
    List<BinaryContent> attachments = attachmentRequests.stream()
        .map(att -> {
          var dto = binaryContentService.create(att);
          return binaryContentRepository.findById(dto.id()).orElseThrow();
        })
        .toList();

    // 3. 메시지 엔티티 생성 (엔티티 생성자 규격 준수)
    Message message = new Message(request.content(), author, channel, attachments);

    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, UUID lastMessageId, int size) {
    // 1. DB에서 데이터 조각(Slice)을 가져옴 (N+1 방지 쿼리 사용)
    Pageable pageable = PageRequest.of(0, size);
    Slice<Message> messageSlice = messageRepository.findMessagesNoOffset(channelId, lastMessageId,
        pageable);

    // 2. 엔티티를 DTO로 변환
    Slice<MessageDto> dtoSlice = messageSlice.map(messageMapper::toDto);

    // 3. 매퍼를 통해 PageResponse로 변환 (idExtractor로 MessageDto의 id를 사용)
    // 여기서 MessageDto::id 가 바로 idExtractor 입니다! [cite: 2026-03-09]
    return pageResponseMapper.fromSlice(dtoSlice, MessageDto::id);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));

    // MessageUpdateRequest의 newContent 필드 사용
    message.update(request.newContent());

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));

    // [물리 자원 해제] 로컬 스토리지 파일 삭제 [cite: 2026-03-04]
    message.getAttachments().forEach(att -> binaryContentService.delete(att.getId()));

    // [JPA 마법] message를 삭제하면 message_attachments 매핑과 메타데이터가 자동 정리됨 [cite: 2026-03-05]
    messageRepository.delete(message);
  }

  @Override
  public MessageDto findById(UUID id) {
    return messageRepository.findById(id)
        .map(messageMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));
  }
}