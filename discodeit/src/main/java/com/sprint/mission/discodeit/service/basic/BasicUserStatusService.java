package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
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

@RequiredArgsConstructor
@Slf4j
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();
    log.debug("사용자 상태 생성 시도, userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자를 찾을 수 없습니다, userId={}", userId);
          return new UserStatusNotFoundException(userId);
        });

    Optional.ofNullable(user.getStatus())
        .ifPresent(status -> {
          log.warn("이미 존재하는 사용자 상태, userId={}", userId);
          throw new UserStatusAlreadyExistsException(userId);
        });

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("사용자 상태 생성 완료, userId={}", userId);

    return userStatusMapper.toDto(userStatus);
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("사용자 상태 조회 시도, userStatusId={}", userStatusId);
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(() -> {
          log.warn("사용자 상태를 찾을 수 없습니다, userStatusId={}", userStatusId);
          return new UserStatusNotFoundException(userStatusId);
        });
  }

  @Override
  public List<UserStatusDto> findAll() {
    log.debug("모든 사용자 상태 조회 시도");
    List<UserStatusDto> result = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
    log.info("모든 사용자 상태 조회 완료, 개수={}", result.size());
    return result;
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.debug("사용자 상태 업데이트 시도, userStatusId={}", userStatusId);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("업데이트할 사용자 상태를 찾을 수 없습니다, userStatusId={}", userStatusId);
          return new UserStatusNotFoundException(userStatusId);
        });

    userStatus.update(newLastActiveAt);
    log.info("사용자 상태 업데이트 완료, userStatusId={}", userStatusId);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    log.debug("사용자 ID로 상태 업데이트 시도, userId={}", userId);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("업데이트할 사용자 상태를 찾을 수 없습니다, userId={}", userId);
          return new UserStatusNotFoundException(userId);
        });

    userStatus.update(newLastActiveAt);
    log.info("사용자 상태 업데이트 완료, userId={}", userId);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.debug("사용자 상태 삭제 시도, userStatusId={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      log.error("삭제할 사용자 상태를 찾을 수 없습니다, userStatusId={}", userStatusId);
      throw new UserStatusNotFoundException(userStatusId);
    }
    userStatusRepository.deleteById(userStatusId);
    log.info("사용자 상태 삭제 완료, userStatusId={}", userStatusId);
  }
}