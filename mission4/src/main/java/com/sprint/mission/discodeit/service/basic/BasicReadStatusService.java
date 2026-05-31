package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.Channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatus.UserAndChannelAlreadyExistException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper mapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));
    if (readStatusRepository.existsByUserIdAndChannelId(user.getId(), channel.getId())) {
      throw new UserAndChannelAlreadyExistException(request.userId(), request.channelId());
    }
    return readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
        .map(status -> {
          status.update(request.lastReadAt());
          return mapper.toDto(readStatusRepository.save(status));
        })
        .orElseGet(() -> {
          ReadStatus newStatus = ReadStatus.builder().user(user).channel(channel)
              .lastReadAt(Instant.now()).build();
          return mapper.toDto(readStatusRepository.save(newStatus));
        });
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new ReadStatusNotFoundException(readStatusId));
    return mapper.toDto(readStatus);

  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatus -> mapper.toDto(readStatus))
        .toList();
  }

  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(
            () -> new ReadStatusNotFoundException(readStatusId));
    readStatus.update(newLastReadAt);
    readStatusRepository.save(readStatus);
    return mapper.toDto(readStatus);
  }

  @Override
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new ReadStatusNotFoundException(readStatusId);
    }
    readStatusRepository.deleteById(readStatusId);
  }
}
