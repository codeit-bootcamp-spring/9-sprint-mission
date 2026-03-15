package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  /**
   * [핵심 수정] 유저가 접근 가능한 모든 채널에 대해 ReadStatus가 존재함을 보장합니다. 데이터가 없으면 즉석에서 생성(Lazy Allocation)하여 프론트엔드
   * crash를 방지합니다.
   */
  @Override
  @Transactional // 조회 시점에 누락된 데이터를 생성해야 하므로 쓰기 권한이 필요합니다.
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));

    // 1. 유저가 참여 중인 모든 채널(공개 + 프라이빗) 목록을 먼저 가져옵니다.
    List<Channel> accessibleChannels = channelRepository.findAllByUserIdOrPublic(userId);

    // 2. 각 채널을 순회하며 ReadStatus가 있는지 확인하고, 없으면 생성합니다.
    return accessibleChannels.stream()
        .map(channel -> readStatusRepository.findByUserIdAndChannelId(userId, channel.getId())
            .orElseGet(() -> {
              // C++의 if(ptr == nullptr) ptr = new Obj(); 와 같은 논리입니다.
              ReadStatus newStatus = new ReadStatus(user, channel, Instant.now());
              return readStatusRepository.save(newStatus);
            })
        )
        .map(readStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new NoSuchElementException("채널을 찾을 수 없습니다."));

    // 중복 생성 방지
    return readStatusRepository.findByUserIdAndChannelId(request.userId(), request.channelId())
        .map(readStatusMapper::toDto)
        .orElseGet(() -> {
          Instant lastReadAt = request.lastReadAt() != null ? request.lastReadAt() : Instant.now();
          ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
          return readStatusMapper.toDto(readStatusRepository.save(readStatus));
        });
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("읽음 상태를 찾을 수 없습니다."));

    if (request.newLastReadAt() != null) {
      readStatus.update(request.newLastReadAt());
    }

    return readStatusMapper.toDto(readStatus);
  }
}