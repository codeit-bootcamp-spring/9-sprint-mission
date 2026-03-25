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
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {
    UUID channelId = messageCreateRequest.channelId();
    UUID authorId = messageCreateRequest.authorId();

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
    return messageMapper.toDto(createdMessage);
  }

  @Override
  public MessageDto find(UUID messageId) {
    Message message = messageRepository.findByIdWithDetails(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    return messageMapper.toDto(message);
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable) {
    channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    List<Message> allMessages = messageRepository.findAllByChannelIdWithDetails(channelId);

    // 트랜잭션 내에서 DTO로 변환 (Lazy loading 방지)
    List<MessageDto> allMessageDtos = allMessages.stream()
        .map(messageMapper::toDto)
        .toList();

    // 페이징 처리
    int pageSize = pageable.getPageSize();
    int pageNumber = pageable.getPageNumber();

    // 커서 기반 페이징: 시작 위치 계산
    int start = pageNumber * pageSize;
    int end = Math.min(start + pageSize, allMessageDtos.size());

    List<MessageDto> content = start >= allMessageDtos.size()
        ? List.of()
        : allMessageDtos.subList(start, end);

    boolean hasNext = end < allMessageDtos.size();

    // 다음 커서: 다음 페이지의 시작 메시지 ID (또는 createdAt)
    // 커서 기반 페이지네이션에서는 마지막 항목의 ID를 커서로 사용
    Object nextCursor = hasNext && !content.isEmpty()
        ? content.get(content.size() - 1).id()
        : null;

    return new PageResponse<>(
        content,
        nextCursor,
        pageSize,
        hasNext,
        (long) allMessageDtos.size()
    );
  }

  @Transactional
  @Override
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    String content = request.newContent();
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));
    message.update(content);
    return messageMapper.toDto(message);
  }

  @Transactional
  @Override
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(Map.of("messageId", messageId)));

    messageRepository.delete(message);
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    String contentType = request.contentType();
    byte[] bytes = request.bytes();

    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    return createdBinaryContent;
  }
}
