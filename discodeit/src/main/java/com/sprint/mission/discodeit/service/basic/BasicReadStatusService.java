package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    UUID userId = request.userId();
    UUID channelId = request.channelId();
    log.debug("읽음 상태 생성 시도, userId={}, channelId={}", userId, channelId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자를 찾을 수 없음, userId={}", userId);
          return new NoSuchElementException("해당 사용자가 존재하지 않습니다: " + userId);
        });

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("채널을 찾을 수 없음, channelId={}", channelId);
          return new NoSuchElementException("해당 채널이 존재하지 않습니다: " + channelId);
        });

    ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId())
        .orElseGet(() -> {
          Instant lastReadAt = request.lastReadAt();
          ReadStatus rs = readStatusRepository.save(new ReadStatus(user, channel, lastReadAt));
          log.info("새 읽음 상태 생성 완료, readStatusId={}, userId={}, channelId={}", rs.getId(), userId, channelId);
          return rs;
        });

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("읽음 상태 조회 시도, readStatusId={}", readStatusId);
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> {
          log.warn("읽음 상태를 찾을 수 없음, readStatusId={}", readStatusId);
          return new NoSuchElementException("해당 ID의 읽음 상태가 존재하지 않습니다: " + readStatusId);
        });
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("사용자의 모든 읽음 상태 조회, userId={}", userId);
    List<ReadStatusDto> result = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
    log.info("사용자의 읽음 상태 조회 완료, userId={}, 조회 수={}", userId, result.size());
    return result;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    log.debug("읽음 상태 업데이트 시도, readStatusId={}, newLastReadAt={}", readStatusId, newLastReadAt);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("업데이트할 읽음 상태를 찾을 수 없음, readStatusId={}", readStatusId);
          return new NoSuchElementException("해당 ID의 읽음 상태가 존재하지 않습니다: " + readStatusId);
        });

    readStatus.update(newLastReadAt);
    log.info("읽음 상태 업데이트 완료, readStatusId={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.debug("읽음 상태 삭제 시도, readStatusId={}", readStatusId);

    if (!readStatusRepository.existsById(readStatusId)) {
      log.error("삭제할 읽음 상태를 찾을 수 없음, readStatusId={}", readStatusId);
      throw new NoSuchElementException("해당 ID의 읽음 상태가 존재하지 않습니다: " + readStatusId);
    }

    readStatusRepository.deleteById(readStatusId);
    log.info("읽음 상태 삭제 완료, readStatusId={}", readStatusId);
  }
}