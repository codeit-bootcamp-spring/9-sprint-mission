package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
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
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.info("새로운 사용자 생성 시작: username = {}, email = {}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("이메일 중복 오류: email = {}", email);
      throw new EmailAlreadyExistsException(ErrorCode.DUPLICATE_EMAIL, Map.of("email", email));
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("사용자명 중복 오류: username = {}", username);
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_USER, Map.of("username", username));
    }

    log.debug("프로필 이미지 처리 시작: profileCreateRequest = {}", optionalProfileCreateRequest);
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent savedContent = binaryContentRepository.save(binaryContent);

          binaryContentStorage.put(binaryContent.getId(), bytes);
          return savedContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    User createdUser = userRepository.save(user);
    UserStatus userStatus = new UserStatus(createdUser, now);
    createdUser.setStatus(userStatusRepository.save(userStatus));

    log.info("사용자 생성 완료: id = {}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.info("사용자 정보 수정 시작: {}", userUpdateRequest);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자를 찾지 못함: {}", userId);
          return new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmailAndIdNot(newEmail, userId)) {
      log.warn("중복되는 이메일: {}", newEmail);
      throw new EmailAlreadyExistsException(ErrorCode.DUPLICATE_EMAIL, Map.of("email", newEmail));
    }
    if (userRepository.existsByUsernameAndIdNot(newUsername, userId)) {
      log.warn("중복되는 사용자명: {}", newUsername);
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_USER,
          Map.of("username", newUsername));
    }

    log.debug("프로필 이미지 업데이트 로직 작동중: {}", optionalProfileCreateRequest);
    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfile())
              .ifPresent(e -> binaryContentRepository.deleteById(e.getId()));

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContentRepository.save(binaryContent);
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();

    user.update(newUsername, newEmail, newPassword, nullableProfileId);
    log.debug("사용자 업데이트 완료: {}", user.getUsername());

    return userMapper.toDto(userRepository.save(user));
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    log.warn("사용자 삭제 시도: {}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("존재하지 않는 사용자ID: {}", userId);
          return new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("userId", userId));
        });

    log.debug("프로필 이미지 삭제");
    Optional.ofNullable(user.getProfile())
        .ifPresent(e -> binaryContentRepository.deleteById(e.getId()));

    log.debug("사용자 상태 삭제: {}", user.getUsername());
    userStatusRepository.deleteByUser_Id(userId);

    log.debug("사용자 삭제: {}", user.getUsername());
    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료");
  }
}
