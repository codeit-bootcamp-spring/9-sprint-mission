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
    log.info("사용자 상태 생성 시작 - userId: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("사용자 상태 생성 실패 - 사용자 없음: {}", userId);
          return new NoSuchElementException("User with id " + userId + " not found");
        });

    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          log.warn("사용자 상태 생성 실패 - 이미 존재: {}", userId);
          throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);

    log.info("사용자 상태 생성 완료 - userStatusId: {}", userStatus.getId());
    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("사용자 상태 조회 - userStatusId: {}", userStatusId);

    return userStatusRepository.findById(userStatusId)
        .map(userStatus -> {
          log.debug("사용자 상태 조회 성공 - userStatusId: {}", userStatusId);
          return userStatusMapper.toDto(userStatus);
        })
        .orElseThrow(() -> {
          log.error("사용자 상태 조회 실패 - 존재하지 않음: {}", userStatusId);
          return new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        });
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("전체 사용자 상태 조회");

    List<UserStatusDto> result = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();

    log.debug("전체 사용자 상태 조회 완료 - 개수: {}", result.size());
    return result;
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.debug("사용자 상태 수정 - userStatusId: {}", userStatusId);

    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.error("사용자 상태 수정 실패 - 존재하지 않음: {}", userStatusId);
          return new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
        });

    userStatus.update(newLastActiveAt);

    log.debug("사용자 상태 수정 완료 - userStatusId: {}", userStatusId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.debug("사용자 상태 수정 (userId 기준) - userId: {}", userId);

    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.error("사용자 상태 수정 실패 - 존재하지 않음: {}", userId);
          return new NoSuchElementException("UserStatus with userId " + userId + " not found");
        });

    userStatus.update(newLastActiveAt);

    log.debug("사용자 상태 수정 완료 (userId 기준) - userId: {}", userId);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.info("사용자 상태 삭제 시작 - userStatusId: {}", userStatusId);

    if (!userStatusRepository.existsById(userStatusId)) {
      log.error("사용자 상태 삭제 실패 - 존재하지 않음: {}", userStatusId);
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }

    userStatusRepository.deleteById(userStatusId);
    log.info("사용자 상태 삭제 완료 - userStatusId: {}", userStatusId);
  }
}