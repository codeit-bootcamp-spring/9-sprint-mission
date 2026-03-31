package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("Fetching/Allocating read statuses for user: {}", userId);
    userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    List<Channel> accessibleChannels = channelRepository.findAllByUserIdOrPublic(userId);

    return accessibleChannels.stream()
        .map(channel -> readStatusRepository.findFirstByUserIdAndChannelId(userId, channel.getId())
            .orElseGet(() -> {
              log.debug("Lazy allocating ReadStatus for user: {} in channel: {}", userId,
                  channel.getId());
              return readStatusRepository.save(new ReadStatus(
                  userRepository.getReferenceById(userId),
                  channel,
                  Instant.now()
              ));
            })
        )
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    log.info("Creating read status for user: {} in channel: {}", request.userId(),
        request.channelId());

    readStatusRepository.findFirstByUserIdAndChannelId(request.userId(), request.channelId())
        .ifPresent(rs -> {
          throw new ReadStatusAlreadyExistsException(request.userId().toString(),
              request.channelId().toString());
        });

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId().toString()));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId().toString()));

    Instant lastReadAt = request.lastReadAt() != null ? request.lastReadAt() : Instant.now();
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);

    return readStatusMapper.toDto(readStatusRepository.save(readStatus));
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.info("Updating lastReadAt for readStatus: {}", readStatusId);
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId.toString()));

    if (request.newLastReadAt() != null) {
      readStatus.update(request.newLastReadAt());
    }

    return readStatusMapper.toDto(readStatus);
  }
}