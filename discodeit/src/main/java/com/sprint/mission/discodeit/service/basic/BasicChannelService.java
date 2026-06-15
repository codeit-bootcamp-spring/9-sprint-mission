package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.ChannelEvent;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ApplicationEventPublisher applicationEventPublisher;

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);

    channelRepository.save(channel);
    ChannelDto channelDto = channelMapper.toDto(channel);
    applicationEventPublisher.publishEvent(new ChannelEvent("channels.created", channelDto));
    log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
    return channelDto;
  }

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("채널 생성 시작: {}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds()).stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    readStatusRepository.saveAll(readStatuses);

    ChannelDto channelDto = channelMapper.toDto(channel);
    applicationEventPublisher.publishEvent(new ChannelEvent("channels.created", channelDto));
    log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
    return channelDto;
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
  }

  @Cacheable(value = "userChannels", key = "#userId")
  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, mySubscribedChannelIds)
        .stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 수정 시작: id={}, request={}", channelId, request);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      throw PrivateChannelUpdateException.forChannel(channelId);
    }
    channel.update(newName, newDescription);
    ChannelDto channelDto = channelMapper.toDto(channel);
    applicationEventPublisher.publishEvent(new ChannelEvent("channels.updated", channelDto));
    log.info("채널 수정 완료: id={}, name={}", channelId, channel.getName());
    return channelDto;
  }

  @CacheEvict(value = "userChannels", allEntries = true)
  @Transactional
  @Override
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시작: id={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));
    ChannelDto channelDto = channelMapper.toDto(channel);

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);

    applicationEventPublisher.publishEvent(new ChannelEvent("channels.deleted", channelDto));
    log.info("채널 삭제 완료: id={}", channelId);
  }
}
