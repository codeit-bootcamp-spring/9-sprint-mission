package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.ChannelUpdatedEvent;
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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final CacheManager cacheManager;
  private final ApplicationEventPublisher eventPublisher;


  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public Channel create(PublicChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    Channel saved = channelRepository.save(channel);
    log.info("public 채널 생성 완료 - 채널: {}", saved);

    var cache = cacheManager.getCache("ChannelList");

    List<User> allUsers = userRepository.findAll();
    allUsers.stream()
        .map(user -> {
          ReadStatus newStatus = new ReadStatus(user, saved, saved.getCreatedAt(), false);
          if (cache != null) {
            String cacheKey = "channels_" + user.getId();
            cache.evict(cacheKey);
          }
          return newStatus;
        })
        .forEach(readStatusRepository::save);
    log.debug("public 채널 유저별 읽음 상태 생성 완료");

    ChannelDto dto = channelMapper.toDto(saved);
    eventPublisher.publishEvent(new ChannelUpdatedEvent("created", dto));

    return saved;
  }

  @Transactional
  @Override
  public Channel create(PrivateChannelCreateRequest request) {
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PRIVATE, name, description);
    Channel createdChannel = channelRepository.save(channel);
    log.info("private 채널 생성 완료 - 채널: {}", createdChannel);
    var cache = cacheManager.getCache("ChannelList");

    request.participantIds().stream()
        .map(userId -> {
          ReadStatus newStatus = new ReadStatus(
              userRepository.findById(userId).orElse(null),
              createdChannel,
              channel.getCreatedAt(),
              true
          );
          if (cache != null) {
            String cacheKey = "channels_" + userId;
            cache.evict(cacheKey);
          }
          log.debug("참가자별 채널 읽음 상태 생성 - 일음 상태: {}", newStatus);
          return newStatus;
        })
        .forEach(readStatusRepository::save);

    ChannelDto dto = channelMapper.toDto(createdChannel);
    eventPublisher.publishEvent(new ChannelUpdatedEvent("created", dto));

    return createdChannel;
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    return channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "ChannelList", key = "'channels_' + #userId")
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new UserNotFoundException(userId);
    }
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    List<Channel> channels;
    if (mySubscribedChannelIds.isEmpty()) {
      channels = channelRepository.findByType(ChannelType.PUBLIC);
    } else {
      channels = channelRepository.findPublicOrSubscribedChannels(mySubscribedChannelIds);
    }
    return channels.stream()
        .map(channelMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    var cache = cacheManager.getCache("ChannelList");

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() ->
        {
          log.warn("채널 검색 실패 - 채널 ID: {}", channelId);
          return new ChannelNotFoundException(channelId);
        });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("채널 수정 실패 - 채널 ID: {}", channelId);
      throw new PrivateChannelUpdateException(channelId);
    }

    channel.update(newName, newDescription);
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(channel);
    for (ReadStatus r : readStatuses) {
      if (cache != null) {
        String cacheKey = "notifications_" + r.getUser().getId();
        cache.evict(cacheKey);
      }
    }
    Channel updated = channelRepository.save(channel);
    ChannelDto dto = channelMapper.toDto(updated);
    eventPublisher.publishEvent(new ChannelUpdatedEvent("updated", dto));
    return updated;
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("채널 검색 실패 - 채널 ID: {}", channelId);
              return new ChannelNotFoundException(channelId);
            });
    var cache = cacheManager.getCache("ChannelList");
    List<ReadStatus> readStatuses = readStatusRepository.findAllByChannel(channel);
    for (ReadStatus r : readStatuses) {
      if (cache != null) {
        String cacheKey = "notifications_" + r.getUser().getId();
        cache.evict(cacheKey);
      }
    }
    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());

    ChannelDto dto = channelMapper.toDto(channel);
    eventPublisher.publishEvent(new ChannelUpdatedEvent("deleted", dto));

    channelRepository.deleteById(channelId);
  }

}
