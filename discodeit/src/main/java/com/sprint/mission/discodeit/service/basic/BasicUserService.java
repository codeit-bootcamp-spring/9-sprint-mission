package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.debug("Create user requested: username={}, email={}", username, email);

    validateDuplicateEmail(email);
    validateDuplicateUsername(username);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);
    user.setStatus(userStatus);

    User createdUser = userRepository.save(user);
    log.info("User created: userId={}, username={}",
        createdUser.getId(), createdUser.getUsername());
    return userMapper.toDto(createdUser);
  }

  @Override
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));
  }

  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("Update user requested: userId={}, newUsername={}, newEmail={}",
        userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    String username = userUpdateRequest.newUsername();
    String email = userUpdateRequest.newEmail();
    validateDuplicateEmail(email, user);
    validateDuplicateUsername(username, user);

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);

    String password = userUpdateRequest.newPassword();
    user.update(username, email, password, nullableProfile);

    log.info("User updated: userId={}", user.getId());

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("Delete user requested: userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    userRepository.delete(user);
    log.info("User deleted: userId={}", userId);
  }

  private void validateDuplicateEmail(String email) {
    if (email != null && userRepository.existsByEmail(email)) {
      log.warn("Duplicate email detected: email={}", email);
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateEmail(String email, User user) {
    if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
      log.warn("Duplicate email detected on update: userId={}, email={}", user.getId(), email);
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateUsername(String username) {
    if (username != null && userRepository.existsByUsername(username)) {
      log.warn("Duplicate username detected: username={}", username);
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private void validateDuplicateUsername(String username, User user) {
    if (username != null && !username.equals(user.getUsername())
        && userRepository.existsByUsername(username)) {
      log.warn("Duplicate username detected on update: userId={}, username={}",
          user.getId(), username);
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    byte[] bytes = request.bytes();
    log.debug("Upload profile requested: fileName={}, contentType={}, size={}",
        request.fileName(), request.contentType(), bytes.length);

    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) bytes.length,
        request.contentType()
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);

    try {
      binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    } catch (RuntimeException ex) {
      log.error("Profile upload failed: binaryContentId={}, fileName={}, contentType={}",
          createdBinaryContent.getId(), request.fileName(), request.contentType(), ex);
      throw ex;
    }
    log.info("Profile uploaded: binaryContentId={}, fileName={}, size={}",
        createdBinaryContent.getId(), createdBinaryContent.getFileName(),
        createdBinaryContent.getSize()
    );
    return createdBinaryContent;
  }
}
