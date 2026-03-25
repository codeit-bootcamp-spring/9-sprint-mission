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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    userRepository.delete(user);
  }

  private void validateDuplicateEmail(String email) {
    if (email != null && userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateEmail(String email, User user) {
    if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistException(Map.of("email", email));
    }
  }

  private void validateDuplicateUsername(String username) {
    if (username != null && userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private void validateDuplicateUsername(String username, User user) {
    if (username != null && !username.equals(user.getUsername())
        && userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistException(Map.of("username", username));
    }
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    byte[] bytes = request.bytes();
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) bytes.length,
        request.contentType()
    );
    BinaryContent createdBinaryContent = binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(createdBinaryContent.getId(), bytes);
    return createdBinaryContent;
  }
}
