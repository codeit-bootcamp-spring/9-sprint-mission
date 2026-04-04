package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    log.debug("사용자 상태 생성 시작: id={}", request);
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
    if (userStatusRepository.findByUserId(userId).isPresent()) {
      throw new UserStatusNotFoundException();
    }

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("사용자 상태 생성 완료: id={}", request);
    return userStatusMapper.toDto(userStatus);
  }

  @Transactional(readOnly = true)
  @Override
  public UserStatusDto find(UUID userStatusId) {
    log.debug("사용자 상태 조회 시작: id={}", userStatusId);
    UserStatusDto userStatusDto = userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(UserStatusNotFoundException::new);
    log.info("사용자 상태 조회 완료: id={}", userStatusId);
    return userStatusDto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserStatusDto> findAll() {
    log.debug("모든 사용자 상태 조회 시작");
    List<UserStatusDto> userStatusDtos = userStatusRepository.findAll().stream()
        .map(userStatusMapper::toDto)
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userStatusDtos.size());
    return userStatusDtos;
  }
  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    log.debug("사용자 상태 수정 시작: id={}, request={}", userStatusId, request);
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(UserStatusNotFoundException::new);
    userStatus.update(newLastActiveAt);

    userStatusRepository.save(userStatus);
    log.info("사용자 상태 수정 완료: id={}, request={}", userStatus, request);
    return userStatusMapper.toDto(userStatus);
  }
  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();
    log.debug("사용자 ID로 상태 수정 시작: userId={}, newLastActiveAt={}",
        userId, newLastActiveAt);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(UserStatusNotFoundException::new);
    userStatus.update(newLastActiveAt);
    userStatusRepository.save(userStatus);
    log.info("사용자 ID로 상태 수정 완료: userId={}", userId);
    return userStatusMapper.toDto(userStatus);
  }
  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    log.debug("사용자 상태 삭제 시작: id={}", userStatusId);
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new UserStatusNotFoundException();
    }
    userStatusRepository.deleteById(userStatusId);
    log.info("사용자 상태 삭제 완료: id={}", userStatusId);
  }
}
