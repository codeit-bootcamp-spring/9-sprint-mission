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
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 상태 생성 실패 (존재하지 않는 사용자 ID): {}", userId);
          return new NoSuchElementException("User with id " + userId + " not found");
        });

    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          log.warn("사용자 상태 생성 실패 (이미 상태 정보가 존재함): 사용자 ID {}", userId);
          throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("사용자 상태 DB 저장 완료 (상태 ID: {}, 사용자 ID: {})", userStatus.getId(), userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> {
          log.warn("사용자 상태 조회 실패 (존재하지 않는 상태 ID): {}", userStatusId);
          return new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        });
  }

  @Override
  public List<UserStatusDto> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("사용자 상태 수정 실패 (존재하지 않는 상태 ID): {}", userStatusId);
          return new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        });

    userStatus.update(newLastActiveAt);
    log.info("사용자 상태 업데이트 완료 (상태 ID: {})", userStatusId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("사용자 상태 수정 실패 (존재하지 않는 사용자 ID): {}", userId);
          return new NoSuchElementException("UserStatus with userId " + userId + " not found");
        });

    userStatus.update(newLastActiveAt);
    log.info("사용자 상태 업데이트 완료 (사용자 ID: {})", userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      log.warn("사용자 상태 삭제 실패 (존재하지 않는 상태 ID): {}", userStatusId);
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }

    userStatusRepository.deleteById(userStatusId);
    log.info("사용자 상태 DB 삭제 완료 (상태 ID: {})", userStatusId);
  }
}