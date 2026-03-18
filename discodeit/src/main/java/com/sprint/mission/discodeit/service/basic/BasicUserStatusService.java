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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional // 데이터 변경이 일어나니까 붙여주기
  @Override
  public UserStatusDto create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(
            () -> new NoSuchElementException("User with id " + userId + " does not exist"));

    if (userStatusRepository.findByUser_Id(userId).isPresent()) {
      throw new IllegalArgumentException("UserStatus with id " + userId + " already exists");
    }

    Instant lastActiveAt = request.lastActiveAt();
    UserStatus userStatus = new UserStatus(user, lastActiveAt);

    return userStatusMapper.toDto(userStatusRepository.save(userStatus));
  }

  @Override
  public UserStatusDto find(UUID userStatusId) {
    return userStatusRepository.findById(userStatusId)
        .map(userStatusMapper::toDto)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
  }

  @Override
  public Slice<UserStatusDto> findAll(Pageable pageable) {
    return userStatusRepository.findAll(pageable)
        .map(userStatusMapper::toDto);
  }

  @Transactional
  @Override
  public UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with id " + userStatusId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus); //변경감지가 동작하므로 DTO로 변환해 반환
  }

  @Transactional
  @Override
  public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(
            () -> new NoSuchElementException("UserStatus with userId " + userId + " not found"));
    userStatus.update(newLastActiveAt);

    return userStatusMapper.toDto(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    if (!userStatusRepository.existsById(userStatusId)) {
      throw new NoSuchElementException("UserStatus with id " + userStatusId + " not found");
    }
    userStatusRepository.deleteById(userStatusId);
  }
}
