package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Transactional
  @Override
  public ReadStatusDto create(ReadStatusCreateRequest request) {
    log.debug("읽음 상태 생성/업데이트 시작: userId={}, channelId={}", request.userId(), request.channelId());

    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> ChannelNotFoundException.withId(channelId));

    Instant lastReadTime = request.lastReadAt() == null ? Instant.now() : request.lastReadAt();

    ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(user.getId(),
            channel.getId())
        .map(existingStatus -> {
          log.debug("기존 읽음 상태 발견 -> 업데이트 진행. 새 시간: {}", lastReadTime);
          existingStatus.update(lastReadTime, true);
          return existingStatus;
        })
        .orElseGet(() -> {
          log.debug("기존 읽음 상태 없음 -> 신규 생성 진행. 시간: {}", lastReadTime);
          return readStatusRepository.save(new ReadStatus(user, channel, lastReadTime));
        });

    log.info("읽음 상태 처리 완료: id={}, userId={}, channelId={}, lastReadAt={}",
        readStatus.getId(), userId, channelId, readStatus.getLastReadAt());
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("읽음 상태 조회 시작: id={}", readStatusId);
    ReadStatusDto dto = readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));
    log.info("읽음 상태 조회 완료: id={}", readStatusId);
    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("사용자별 읽음 상태 목록 조회 시작: userId={}", userId);
    List<ReadStatusDto> dtos = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
    log.info("사용자별 읽음 상태 목록 조회 완료: userId={}, 조회된 항목 수={}", userId, dtos.size());
    return dtos;
  }


  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.debug("읽음 상태 및 알림 설정 수정 시작: id={}, newLastReadAt={}, newNotificationEnabled={}",
        readStatusId, request.newLastReadAt(), request.newNotificationEnabled());

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> ReadStatusNotFoundException.withId(readStatusId));

    readStatus.update(request.newLastReadAt(), request.newNotificationEnabled());

    log.info("읽음 상태 및 알림 설정 수정 완료: id={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.debug("읽음 상태 삭제 시작: id={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      throw ReadStatusNotFoundException.withId(readStatusId);
    }
    readStatusRepository.deleteById(readStatusId);
    log.info("읽음 상태 삭제 완료: id={}", readStatusId);
  }
}
