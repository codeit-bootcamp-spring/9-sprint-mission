package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    return channelMapper.toDto(channelRepository.save(channel));
  }

  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    request.participantIds().stream()
        .map(userId -> {

          User user = userRepository.findById(userId)
              .orElseThrow(
                  () -> new NoSuchElementException("User with id " + userId + " does not exist"));

          return new ReadStatus(user, createdChannel, Instant.now());
        })
        .forEach(readStatusRepository::save);

    return channelMapper.toDto(createdChannel);
  }

  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
  }

  @Override
  public Slice<ChannelDto> findAllByUserId(UUID userId, Pageable pageable) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    if (mySubscribedChannelIds.isEmpty()) {
      return channelRepository.findByType(ChannelType.PUBLIC, pageable)
          .map(channelMapper::toDto);
    } else {
      return channelRepository.findByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds,
              pageable)
          .map(channelMapper::toDto);
    }
  }

  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw new IllegalArgumentException("Private channel cannot be updated");
    }
    channel.update(newName, newDescription);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> new NoSuchElementException("Channel with id " + channelId + " not found"));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());
    channelRepository.deleteById(channelId);
  }

}
