package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;
  private final Map<UUID, Instant> lastUpdateCache = new ConcurrentHashMap<>();

  @Override
  @Transactional
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + userId + " does not exist"));

    if (userStatusRepository.findByUser_Id(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus for user id " + userId + " already exists");
    }

    UserStatus userStatus = new UserStatus(user,
        request.lastActiveAt() != null ? request.lastActiveAt() : Instant.now());
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));

    userStatus.update(request.newLastActiveAt());
    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant now = Instant.now();
    Instant lastUpdate = lastUpdateCache.get(userId);
    if (lastUpdate != null && Duration.between(lastUpdate, now).getSeconds() < 10) {

      return userStatusRepository.findByUser_Id(userId)
          .map(userStatusMapper::toDto)
          .orElseThrow(() -> new NoSuchElementException("UserStatus not found"));
    }
    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));

    userStatus.update(request.newLastActiveAt());
    UserStatus saved = userStatusRepository.save(userStatus);
    lastUpdateCache.put(userId, now);

    return userStatusMapper.toDto(saved);
//    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
//        .orElseThrow(
//            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
//
//    userStatus.update(request.newLastActiveAt());
//    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}