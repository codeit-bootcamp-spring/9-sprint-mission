package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
    log.info("Creating initial status for user: {}", userId);

    if (userStatusRepository.findByUserId(userId).isPresent()) {
      log.warn("UserStatus already exists for user: {}", userId);
      return;
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId.toString()));

    userStatusRepository.save(new UserStatus(user));
    log.info("UserStatus created successfully for user: {}", userId);
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.info("Updating status for user: {}", userId);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.error("Update failed: UserStatus not found for user {}", userId);
          return new UserStatusNotFoundException(userId.toString());
        });

    if (request.newLastActiveAt() != null) {
      userStatus.updateLastActiveAt(request.newLastActiveAt());
    }

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public boolean isUserOnline(UUID userId) {
    log.debug("Checking online status for user: {}", userId);
    return userStatusRepository.findByUserId(userId)
        .map(UserStatus::isOnline)
        .orElse(false);
  }

  @Override
  public UserStatusDto findByUserId(UUID userId) {
    log.debug("Finding status for user: {}", userId);
    return userStatusRepository.findByUserId(userId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new UserStatusNotFoundException(userId.toString()));
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("Fetching all user statuses");
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public void deleteByUserId(UUID userId) {
    log.info("Deleting status for user: {}", userId);
    userStatusRepository.findByUserId(userId)
        .ifPresent(userStatusRepository::delete);
  }
}