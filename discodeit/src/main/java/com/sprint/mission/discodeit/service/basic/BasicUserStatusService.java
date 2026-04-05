package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;
  private final UserStatusMapper userStatusMapper;

  @Transactional
  @Override
  public UserStatusResponse create(UserStatusCreateRequest request) {
    UUID userId = request.userId();

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
    if (userStatusRepository.findByUser_Id(userId).isPresent()) {
      throw new UserStatusAlreadyExistException(Map.of("userId", userId));
    }

    UserStatus userStatus = new UserStatus(user, request.lastActiveAt());
    UserStatus createdUserStatus = userStatusRepository.save(userStatus);
    return userStatusMapper.toResponse(createdUserStatus);
  }

  @Override
  public UserStatusResponse find(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(
            Map.of("userStatusId", userStatusId)));
    return userStatusMapper.toResponse(userStatus);
  }

  @Override
  public List<UserStatusResponse> findAll() {
    return userStatusRepository.findAll().stream()
        .map(userStatusMapper::toResponse)
        .toList();
  }

  @Transactional
  @Override
  public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(
            Map.of("userStatusId", userStatusId)));
    userStatus.update(request.newLastActiveAt());
    return userStatusMapper.toResponse(userStatus);
  }

  @Transactional
  @Override
  public UserStatusResponse updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    UserStatus userStatus = userStatusRepository.findByUser_Id(userId)
        .orElseThrow(() -> new UserStatusNotFoundException(
            Map.of("userId", userId)));
    userStatus.update(request.newLastActiveAt());
    return userStatusMapper.toResponse(userStatus);
  }

  @Transactional
  @Override
  public void delete(UUID userStatusId) {
    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(
            Map.of("userStatusId", userStatusId)));
    userStatusRepository.delete(userStatus);
  }
}
