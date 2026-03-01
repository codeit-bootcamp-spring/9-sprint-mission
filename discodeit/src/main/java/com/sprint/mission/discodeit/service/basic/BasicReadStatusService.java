package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
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
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional
  public ReadStatus create(ReadStatusCreateRequest request) {
    if (!userRepository.existsById(request.getUserId()) ||
        !channelRepository.existsById(request.getChannelId())) {
      throw new IllegalArgumentException("존재하지 않는 유저 또는 채널입니다.");
    }

    if (readStatusRepository.findByUserIdAndChannelId(request.getUserId(), request.getChannelId())
        .isPresent()) {
      throw new IllegalStateException("이미 존재하는 읽음 상태입니다.");
    }

    Instant lastReadAt = request.getLastReadAt() != null ? request.getLastReadAt() : Instant.now();
    ReadStatus readStatus = new ReadStatus(request.getUserId(), request.getChannelId(), lastReadAt);
    return readStatusRepository.save(readStatus);
  }

  @Override
  @Transactional
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new NoSuchElementException("ReadStatus not found"));

    if (request.getNewLastReadAt() != null) {
      readStatus.setLastReadAt(request.getNewLastReadAt());
    }

    readStatus.recordUpdate();
    return readStatusRepository.save(readStatus);
  }
}