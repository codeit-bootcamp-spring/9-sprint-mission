package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Override
  @Transactional
  public void create(UUID userId) {
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new IllegalStateException("해당 유저의 상태가 이미 존재합니다.");
    }
    User user = userRepository.findById(userId).orElseThrow();
    userStatusRepository.save(new UserStatus(user));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> new NoSuchElementException("유저 상태를 찾을 수 없습니다."));

    // API 명세 v1.1 필드명 newLastActiveAt 반영
    if (request.newLastActiveAt() != null) {
      userStatus.updateLastActiveAt(request.newLastActiveAt());
    }

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public boolean isUserOnline(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(UserStatus::isOnline)
        .orElse(false);
  }

  @Override
  public UserStatusDto findByUserId(UUID userId) {
    return userStatusRepository.findByUserId(userId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("유저 상태를 찾을 수 없습니다."));
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void deleteByUserId(UUID userId) {
    userStatusRepository.findByUserId(userId)
        .ifPresent(userStatusRepository::delete);
  }
}