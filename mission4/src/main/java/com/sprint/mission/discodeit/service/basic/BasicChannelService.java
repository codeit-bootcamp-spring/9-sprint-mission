package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.entity.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.entity.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.exception.Channel.ChannelAlreadyExistsException;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.Channel.PrivateChannelUpdateException;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final ChannelMapper channelMapper;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  @PreAuthorize("hasRole('CHANNEL_MANAGER')")
  @CacheEvict(value = "channels", allEntries = true)
  public ChannelDto create(PublicChannelCreateRequest request) {
    String name = request.name();
    if (channelRepository.existsByName(name)) {
      log.warn("채널 생성 실패 - 이미 존재하는 이름: {}", name);
      throw new ChannelAlreadyExistsException(name);
    }
    String description = request.description();
    Channel channel = new Channel(ChannelType.PUBLIC, name, description);
    channelRepository.save(channel);
    log.info("공용 채널 생성 성공- 채널 이름: {},채널 설명: {}", name, description);

    ChannelDto dto = channelMapper.toDto(channel);
    // 프론트가 SSE "channels.created" 이벤트를 구독하고 있습니다. 트랜잭션이 커밋된
    // 뒤에만 발송되도록 이벤트로 발행합니다(공용 채널은 전체 브로드캐스트).
    eventPublisher.publishEvent(new ChannelCreatedEvent(dto, null));
    return dto;

  }

  @Override
  @Transactional
  @CacheEvict(value = "channels", allEntries = true)
  public ChannelDto create(PrivateChannelCreateRequest request) {
    Channel channel = new Channel(ChannelType.PRIVATE, null, null);
    List<User> participants = userRepository.findAllById(request.participantIds());
    if (participants.size() != request.participantIds().size()) {
      List<UUID> ids = participants.stream().map(User::getId).toList();
      List<UUID> missingIds = request.participantIds().stream().filter(id -> !ids.contains(id))
          .toList();
      log.warn("채널 생성 실패 - 존재하지 않는 참가자 포함. 요청 인원: {}, 검색된 인원: {}, 누락된 ID: {}",
          request.participantIds().size(),
          participants.size(),
          missingIds);
      throw new DiscodeitException(ErrorCode.PARTICIPANTS_NOT_FOUND);
    }
    participants.forEach(user -> {
      ReadStatus readStatus = ReadStatus.builder().user(user).channel(channel)
          .lastReadAt(Instant.now()).notificationEnabled(true).build();
      channel.getReadStatuses().add(readStatus);
    });

    channelRepository.save(channel);
    log.info("프라이빗 채널 생성 완료 - 채널 Id:{},채널 참가 인원 수:{}", channel.getId(), participants.size());

    ChannelDto dto = channelMapper.toDto(channel);
    // 프라이빗 채널은 참가자한테만 실시간으로 알려줍니다. 트랜잭션 커밋 후에만 이벤트가
    // 나가므로, 참가자가 이 이벤트를 받은 시점에는 채널/읽음상태가 이미 DB에 확정되어
    // 있음이 보장됩니다(생성 직후 바로 메시지를 보내도 놓치지 않습니다).
    List<UUID> participantIds = participants.stream().map(User::getId).toList();
    eventPublisher.publishEvent(new ChannelCreatedEvent(dto, participantIds));
    return dto;
  }

  @Override
  @Transactional(readOnly = true)
  public ChannelDto find(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new ChannelNotFoundException(channelId));
    return channelMapper.toDto(channel);

  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "channels", key = "#userId")
  public List<ChannelDto> findAllByUserId(UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.warn("채널 조회 실패 - 존재하지 않는 유저Id:{}", userId);
      throw new UserNotFoundException(userId);

    }
    List<Channel> mySubscribedChannels = channelRepository.findAllAccessibleByUserId(userId);
    if (mySubscribedChannels.isEmpty()) {
      return List.of();
    }

    List<UUID> channelIds = mySubscribedChannels.stream().map(Channel::getId).toList();
    readStatusRepository.findAllByChannelIdIn(channelIds);

    return mySubscribedChannels.stream()
        .map(channel -> channelMapper.toDto(channel))
        .toList();

  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHANNEL_MANAGER')")
  @CacheEvict(value = "channels", allEntries = true)
  public ChannelDto update(UUID channelId, PublicChannelUpdateRequest request) {
    String newName = request.newName();
    String newDescription = request.newDescription();
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(
            () -> {
              log.warn("채널 조회 실패 - 존재하지 않는 ID: {}", channelId);
              return new ChannelNotFoundException(channelId);
            });
    if (channel.getType().equals(ChannelType.PRIVATE)) {
      log.warn("채널 업데이트 실패(프라이빗 채널 수정 시도)");
      throw new PrivateChannelUpdateException(channelId);
    }

    if (!channel.getName().equals(request.newName())) {
      if (channelRepository.existsByName(request.newName())) {
        log.warn("채널 업데이트 실패 - 이미 존재하는 채널 이름:{}", newName);
        throw new ChannelAlreadyExistsException(request.newName());
      }
    }

    channel.update(newName, newDescription);
    log.info("채널 업데이트 성공- 새 채널 이름:{}, 새 채널 설명:{}", newName, newDescription);

    ChannelDto dto = channelMapper.toDto(channel);
    // 공용 채널만 여기까지 올 수 있으므로(프라이빗은 위에서 예외) 전체 브로드캐스트.
    eventPublisher.publishEvent(new ChannelUpdatedEvent(dto));
    return dto;
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN') or hasRole('CHANNEL_MANAGER')")
  @CacheEvict(value = "channels", allEntries = true)
  public void delete(UUID channelId) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널 삭제 실패 - 존재하지 않는 채널 Id:{}", channelId);
          return new ChannelNotFoundException(channelId);
        });

    // readStatuses가 삭제되기 전에 프라이빗 채널 참가자 목록을 먼저 확보해둡니다.
    List<UUID> participantIds = channel.getType().equals(ChannelType.PRIVATE)
        ? channel.getReadStatuses().stream().map(rs -> rs.getUser().getId()).toList()
        : null;

    messageRepository.deleteAllByChannelId(channelId);
    readStatusRepository.deleteAllByChannelId(channelId);
    channelRepository.deleteById(channelId);
    log.info("채널 삭제 성공-삭제된 채널 Id:{}", channelId);

    eventPublisher.publishEvent(new ChannelDeletedEvent(channelId, participantIds));
  }


}
