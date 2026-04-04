package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readStatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

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
    log.debug("읽음 상태 생성 시작: userId={}, channelId={}", request.userId(), request.channelId());
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(ChannelNotFoundException::new);

    ReadStatus readStatus = readStatusRepository.findByUserIdAndChannelId(userId, channelId)
        .orElseGet(() -> {
            Instant lastReadAt = request.lastReadAt();
            ReadStatus readStatus1 = new ReadStatus(user, channel, lastReadAt);
            return readStatusRepository.save(readStatus1);
        });
    log.info("읽음 상태 생성 완료: id={}, userId={}, channelId={}",
        readStatus.getId(), userId, channelId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("읽음 상태 조회 시작: id={}", readStatusId);
    ReadStatusDto readStatusDto = readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto)
        .orElseThrow(ReadStatusNotFoundException::new);
    log.info("읽음 상태 조회 완료: id={}", readStatusId);
    return readStatusDto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("모든 읽음 상태 조회 시작: id={}", userId);
    List<ReadStatusDto> readStatusDtos = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();
    log.info("모든 읽음 상태 조회 완료: 총 {}개", readStatusDtos.size());
    return readStatusDtos;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.debug("읽음 상태 수정 시작: id={} request={}", readStatusId,request);
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(ReadStatusNotFoundException::new);
    readStatus.update(newLastReadAt);
    readStatusRepository.save(readStatus);
    log.info("읽음 상태 수정 완료 : id={}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.debug("읽음 상태 삭제 시작: id={}", readStatusId);
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new ReadStatusNotFoundException();
    }
    readStatusRepository.deleteById(readStatusId);
    log.info("읽음 상태 삭제 완료: id={}", readStatusId);
  }
}
