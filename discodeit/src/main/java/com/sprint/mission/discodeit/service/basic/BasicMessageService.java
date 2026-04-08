package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.ForbiddenException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentService binaryContentService;
  private final ReadStatusRepository readStatusRepository;
  private final MessageMapper messageMapper;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto send(MessageCreateRequest request,
      List<BinaryContentCreateRequest> attachmentRequests) {
    User author = userRepository.findById(request.authorId())
        .orElseThrow(() -> new UserNotFoundException(request.authorId().toString()));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId().toString()));

    if (!channel.getParticipants().contains(author)) {
      if (channel.getType() == ChannelType.PUBLIC) {
        channel.addParticipant(author);
        readStatusRepository.save(new ReadStatus(author, channel, Instant.now()));
        log.info("User {} automatically joined public channel {}", author.getUsername(),
            channel.getName());
      } else {
        throw new ForbiddenException("채널 참여자가 아닙니다.");
      }
    }

    List<BinaryContent> attachments = attachmentRequests.stream()
        .map(att -> {
          var dto = binaryContentService.create(att);
          return binaryContentRepository.findById(dto.id())
              .orElseThrow(() -> new BinaryContentNotFoundException(dto.id().toString()));
        }).toList();

    Message message = new Message(request.content(), author, channel, attachments);
    return messageMapper.toDto(messageRepository.save(message));
  }

  @Override
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, Instant cursor, int size) {
    Pageable pageable = PageRequest.of(0, size);

    Slice<Message> messageSlice = messageRepository.findMessagesNoOffset(channelId, cursor,
        pageable);

    return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toDto),
        MessageDto::createdAt);
  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId.toString()));
    message.update(request.newContent());
    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId.toString()));
    message.getAttachments().forEach(att -> binaryContentService.delete(att.getId()));
    messageRepository.delete(message);
  }

  @Override
  public MessageDto findById(UUID id) {
    return messageRepository.findById(id).map(messageMapper::toDto)
        .orElseThrow(() -> new MessageNotFoundException(id.toString()));
  }
}