package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;

  @Override
  @Transactional
  public void create(UUID userId) {
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalStateException("해당 유저의 상태가 이미 존재합니다.");
    }
    UserStatus status = new UserStatus(userId);
    userStatusRepository.save(status);
  }

  @Override
  @Transactional
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("User status not found"));

    if (request.getNewLastActiveAt() != null) {
      userStatus.setLastActiveAt(request.getNewLastActiveAt());
    } else {
      userStatus.updateLastActiveAt();
    }

    return userStatusRepository.save(userStatus);
  }

  @Override
  public boolean isUserOnline(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(UserStatus::isOnline) // 엔티티 내부의 5분 판정 로직 사용
        .orElse(false);
  }

  @Override
  public UserStatus findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("User status not found"));
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAll();
  }

  @Override
  @Transactional
  public void deleteByUserId(UUID userId) {
    userStatusRepository.findByUserId(userId)
        .ifPresent(s -> userStatusRepository.deleteById(s.getId()));
  }
}