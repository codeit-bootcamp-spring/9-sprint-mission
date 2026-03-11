package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final PageResponseMapper pageResponseMapper;

  @Override
  @Transactional
  public MessageDto create(MessageCreateRequest messageCreateRequest,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    User author = userRepository.findById(messageCreateRequest.authorId())
        .orElseThrow(() -> new NoSuchElementException("Author not found"));
    Channel channel = channelRepository.findById(messageCreateRequest.channelId())
        .orElseThrow(() -> new NoSuchElementException("Channel not found"));

    Message message = new Message(messageCreateRequest.content(), channel, author);
    Message savedMessage = messageRepository.save(message);

    if (binaryContentCreateRequests != null && !binaryContentCreateRequests.isEmpty()) {
      binaryContentCreateRequests.forEach(request -> {
        BinaryContent binaryContent = new BinaryContent(
            request.fileName(),
            request.contentType(),
            request.bytes(),
            savedMessage
        );
        binaryContentRepository.save(binaryContent);
        binaryContentStorage.put(binaryContent.getId(), request.bytes());
      });
    }

    return messageMapper.toDto(savedMessage);
  }

  @Override
  public MessageDto find(UUID messageId) {
    return messageRepository.findById(messageId)
        .map(messageMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("Message not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, UUID cursor, int size) {
    PageRequest pageRequest = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    Slice<Message> messageSlice = messageRepository.findAllByCursor(channelId, cursor, pageRequest);
    Slice<MessageDto> dtoSlice = messageSlice.map(messageMapper::toDto);

    return pageResponseMapper.fromSlice(dtoSlice);
  }
//  @Override
//  @Transactional(readOnly = true)
//  public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int pageNumber) {
//
//    PageRequest pageRequest = PageRequest.of(pageNumber, 50,
//        Sort.by(Sort.Direction.DESC, "createdAt"));
//    Slice<Message> messageSlice = messageRepository.findAllByChannelId(channelId, pageRequest);
//
//    return pageResponseMapper.fromSlice(messageSlice.map(messageMapper::toDto));
//
//  }

  @Override
  @Transactional
  public MessageDto update(UUID messageId, MessageUpdateRequest request,
      List<BinaryContentCreateRequest> binaryContentCreateRequests) {

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new NoSuchElementException("Message not found"));

    message.update(request.newContent());

    return messageMapper.toDto(message);
  }

  @Override
  @Transactional
  public void delete(UUID messageId) {
    if (!messageRepository.existsById(messageId)) {
      throw new NoSuchElementException("Message not found");
    }
    messageRepository.deleteById(messageId);
  }


}