package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusMapper readStatusMapper;

  @Override
  @Transactional
  public ReadStatusDto create(ReadStatusCreateRequest request) { // 🚩 리턴 타입 변경
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " does not exist");
    }
    if (!channelRepository.existsById(channelId)) {
      throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
    }

    // 중복 체크 로직 (기존 유지)
    if (readStatusRepository.findAllByUserId(userId).stream()
        .anyMatch(readStatus -> readStatus.getChannelId().equals(channelId))) {
      throw new IllegalArgumentException(
          "ReadStatus already exists for this user and channel");
    }

    // 엔티티 생성 시 요청받은 시간(혹은 now) 사용
    ReadStatus readStatus = new ReadStatus(userId, channelId,
        request.lastReadAt() != null ? request.lastReadAt() : Instant.now());

    return readStatusMapper.toDto(readStatusRepository.save(readStatus)); // 🚩 Mapper 적용
  }

  @Override
  @Transactional(readOnly = true)
  public ReadStatusDto find(UUID readStatusId) { // 🚩 리턴 타입 변경
    return readStatusRepository.findById(readStatusId)
        .map(readStatusMapper::toDto) // 🚩 Mapper 적용
        .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReadStatusDto> findAllByUserId(UUID userId) { // 🚩 리턴 타입 변경
    return readStatusRepository.findAllByUserId(userId).stream()
        .map(readStatusMapper::toDto) // 🚩 Mapper 적용
        .toList();
  }

  @Override
  @Transactional
  public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateRequest request) { // 🚩 리턴 타입 변경
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

    readStatus.update(request.newLastReadAt());

    return readStatusMapper.toDto(readStatusRepository.save(readStatus)); // 🚩 Mapper 적용
  }

  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    if (!readStatusRepository.existsById(readStatusId)) {
      throw new NoSuchElementException("ReadStatus not found");
    }
    readStatusRepository.deleteById(readStatusId);
  }
}