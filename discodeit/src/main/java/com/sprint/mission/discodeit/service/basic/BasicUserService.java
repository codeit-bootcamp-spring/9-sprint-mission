package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.error.UserAlreadyExistsException;
import com.sprint.mission.discodeit.error.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
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
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;


  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("사용자 생성 요청 수신 - Username: {}, Email: {}", userCreateRequest.username(),
        userCreateRequest.email());

    if (userRepository.existsByEmail(userCreateRequest.email())) {
      log.warn("사용자 생성 실패 - 이미 존재하는 이메일: {}", userCreateRequest.email());
      throw new UserAlreadyExistsException(Map.of("email", userCreateRequest.email()));
    }
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.debug("프로필 이미지 업로드 시작: {}", profileRequest.fileName());
          BinaryContent binaryContent = new BinaryContent(profileRequest.fileName(),
              (long) profileRequest.bytes().length, profileRequest.contentType());
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(null);

    User user = new User(userCreateRequest.username(), userCreateRequest.email(),
        userCreateRequest.password(), nullableProfile);
    userRepository.save(user);

    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);
    log.debug("사용자 초기 상태 생성 완료 - UserID: {}", user.getId());

    log.info("사용자 생성 완료 - ID: {}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("사용자 수정 요청 수신 - ID: {}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 수정 실패 - 존재하지 않는 ID: {}", userId);
          return new UserNotFoundException(Map.of("userId", userId));
        });
    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
      log.warn("사용자 수정 실패 - 중복 이메일: {}", newEmail);
      throw new UserAlreadyExistsException(Map.of("email", newEmail));
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          log.debug("프로필 업데이트: {}", profileRequest.fileName());
          BinaryContent binaryContent = new BinaryContent(profileRequest.fileName(),
              (long) profileRequest.bytes().length, profileRequest.contentType());
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), profileRequest.bytes());
          return binaryContent;
        })
        .orElse(user.getProfile());

    user.update(newUsername, newEmail, userUpdateRequest.newPassword(), nullableProfile);

    log.info("사용자 수정 완료 - ID: {}", userId);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.info("사용자 삭제 요청 수신 - ID: {}", userId);
    if (userRepository.existsById(userId)) {
      log.warn("사용자 삭제 실패 - 존재하지 않는 ID: {}", userId);
      throw new UserNotFoundException(Map.of("userId", userId));
    }
    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료 - ID: {}", userId);
  }
}
