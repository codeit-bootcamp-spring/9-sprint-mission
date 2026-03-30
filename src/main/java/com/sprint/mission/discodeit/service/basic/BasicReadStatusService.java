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
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    log.debug("읽음 상태 생성 시도 - userId: {}, channelId: {}", userId, channelId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("읽음 상태 생성 실패 - 사용자 없음: {}", userId);
          return new NoSuchElementException("User with id " + userId + " does not exist");
        });

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.error("읽음 상태 생성 실패 - 채널 없음: {}", channelId);
          return new NoSuchElementException("Channel with id " + channelId + " does not exist");
        });

    ReadStatus readStatus = readStatusRepository
        .findByUserIdAndChannelId(user.getId(), channel.getId())
        .orElseGet(() -> {
          Instant lastReadAt = request.lastReadAt();
          log.debug("읽음 상태 신규 생성 - lastReadAt: {}", lastReadAt);
          return readStatusRepository.save(new ReadStatus(user, channel, lastReadAt));
        });

    return readStatusMapper.toDto(readStatus);
  }

  @Override
  public ReadStatusDto find(UUID readStatusId) {
    log.debug("읽음 상태 조회 - readStatusId: {}", readStatusId);

    return readStatusRepository.findById(readStatusId)
        .map(readStatus -> {
          log.debug("읽음 상태 조회 성공 - readStatusId: {}", readStatusId);
          return readStatusMapper.toDto(readStatus);
        })
        .orElseThrow(() -> {
          log.error("읽음 상태 조회 실패 - 존재하지 않음: {}", readStatusId);
          return new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        });
  }

  @Override
  public List<ReadStatusDto> findAllByUserId(UUID userId) {
    log.debug("읽음 상태 목록 조회 - userId: {}", userId);

    List<ReadStatusDto> result = readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto)
        .toList();

    log.debug("읽음 상태 목록 조회 완료 - 개수: {}", result.size());
    return result;
  }

  @Transactional
  @Override
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) {
    log.debug("읽음 상태 수정 - readStatusId: {}", readStatusId);

    Instant newLastReadAt = request.newLastReadAt();

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.error("읽음 상태 수정 실패 - 존재하지 않음: {}", readStatusId);
          return new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
        });

    readStatus.update(newLastReadAt);

    log.debug("읽음 상태 수정 완료 - readStatusId: {}", readStatusId);
    return readStatusMapper.toDto(readStatus);
  }

  @Transactional
  @Override
  public void delete(UUID readStatusId) {
    log.info("읽음 상태 삭제 시작 - readStatusId: {}", readStatusId);

    if (!readStatusRepository.existsById(readStatusId)) {
      log.error("읽음 상태 삭제 실패 - 존재하지 않음: {}", readStatusId);
      throw new NoSuchElementException("ReadStatus with id " + readStatusId + " not found");
    }

    readStatusRepository.deleteById(readStatusId);
    log.info("읽음 상태 삭제 완료 - readStatusId: {}", readStatusId);
  }
}