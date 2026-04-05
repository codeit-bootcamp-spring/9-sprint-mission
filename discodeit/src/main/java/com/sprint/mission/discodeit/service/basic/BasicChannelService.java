package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;

  @Transactional
  @Override
  public ChannelResponse create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    log.debug("Create public channel requested: name={}, description={}", name, description);

    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    Channel createdChannel = channelRepository.save(channel);
    log.info("Public channel created: id={}, name={}", createdChannel.getId(),
        createdChannel.getName());
    return channelMapper.toResponse(createdChannel);
  }

  @Transactional
  @Override
  public ChannelResponse create(PrivateChannelCreateRequest request) {
    log.debug("Create private channel requested: participantIds={}", request.participantIds());
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    Channel createdChannel = channelRepository.save(channel);

    List<User> participants = userRepository.findAllById(request.participantIds());
    if (participants.size() != request.participantIds().size()) {
      throw new UserNotFoundException(Map.of("participantIds", request.participantIds()));
    }
    participants.stream()
        .map(user -> new ReadStatus(user, createdChannel, createdChannel.getCreatedAt()))
        .forEach(readStatusRepository::save);

    log.info("Private channel created: id={}", createdChannel.getId());
    return channelMapper.toResponse(createdChannel);
  }

  @Override
  public ChannelResponse find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toResponse)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUser_Id(userId).stream()
        .map(readStatus -> readStatus.getChannel().getId())
        .toList();

    return channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(channelMapper::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request) {
    String name = request.newName();
    String description = request.newDescription();
    log.debug("Update channel requested: channelId={}, newName={}, newDescription={}",
        channelId, name, description);

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("Private channel update not allowed: channelId={}", channelId);
      throw new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }
    channel.update(name, description);
    log.info("Channel updated: channelId={}", channelId);
    return channelMapper.toResponse(channel);
  }

  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.debug("Delete channel requested: channelId={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(Map.of("channelId", channelId)));

    messageRepository.deleteAllByChannel_Id(channel.getId());
    readStatusRepository.deleteAllByChannel_Id(channel.getId());

    channelRepository.delete(channel);
    log.info("Channel deleted: channelId={}", channelId);
  }
}
