package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;
  private final UserRepository userRepository;

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto create(PublicChannelCreateRequest request) {
    log.debug("Public 채널 생성 시작: {}", request);
    String name = request.name();
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);
    log.info("Public 채널을 생성 완료: id={}, channlName={}", channel.getId(), channel.getName());
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto create(PrivateChannelCreateRequest request) {
    log.debug("Private 채널 생성 시작: {}", request);
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(channel);

    List<ReadStatus> readStatuses = userRepository.findAllById(request.participantIds())
        .stream()
        .map(user -> new ReadStatus(user, channel, channel.getCreatedAt()))
        .toList();
    readStatusRepository.saveAll(readStatuses);
    log.info("Private 채널을 생성 완료: id={}", channel.getId());
    return channelMapper.toDto(channel);
  }

  @Transactional(readOnly = true)
  @Override
  public ChannelDto find(UUID channelId) {
    log.debug("채널 조회 시작: id={}", channelId);
    ChannelDto channelDto = channelRepository.findById(channelId)
        .map(channelMapper::toDto)
        .orElseThrow(
            () -> new ChannelNotFoundException(ErrorCode.CHANNEL_NOT_FOUND, Map.of("attemptedChannelId", channelId)));
    log.info("채널 조회 완료: id={}", channelId);
    return channelDto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    log.debug("모든 Public 채널 조회 시작");
    List<UUID> mySubscribedChannelIds = readStatusRepository.findAllByUserId(userId).stream()
        .map(ReadStatus::getChannel)
        .map(Channel::getId)
        .toList();

    List<ChannelDto> channelDtos = channelRepository.findAll().stream()
        .filter(channel ->
            channel.getType().equals(ChannelType.PUBLIC)
                || mySubscribedChannelIds.contains(channel.getId())
        )
        .map(channelMapper::toDto)
        .toList();
    log.info("모든 Public 채널 조회 완료: 총 {}개", channelDtos.size());
    return channelDtos;
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    log.debug("채널 수정 시작: id={}, request={}", channelId, request);
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(ChannelNotFoundException::new);
    if (channel.getType() == ChannelType.PRIVATE) {
      throw new PrivateChannelUpdateException(ErrorCode.PRIVATE_CHANNEL_UPDATE, Map.of("targetChannelId", channelId));
    }
    channel.update(newName, newDescription);
    channelRepository.save(channel);
    log.info("채널을 수정 완로: id={}", channelId);
    return channelMapper.toDto(channel);
  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  public void delete(UUID channelId) {
    log.debug("채널 삭제 시작: id={}", channelId);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(ChannelNotFoundException::new);

    messageRepository.deleteAllByChannelId(channel.getId());
    readStatusRepository.deleteAllByChannelId(channel.getId());
    log.info("채널을 삭제 완료: id={}", channelId);
    channelRepository.deleteById(channelId);
  }
}
