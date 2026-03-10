package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  public UserDto create(UserCreateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest) {

    validateDuplicate(request.username(), request.email());

    BinaryContent profile = profileRequest
        .map(this::saveProfile)
        .orElse(null);

    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profile
    );

    UserStatus status = new UserStatus(user, Instant.now());
    user.setStatus(status); // cascade로 자동 저장

    userRepository.save(user);

    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() ->
            new NoSuchElementException("User with id " + userId + " not found"));

    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  public UserDto update(UUID userId,
      UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> profileRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() ->
            new NoSuchElementException("User with id " + userId + " not found"));

    validateDuplicate(request.newUsername(), request.newEmail());

    BinaryContent newProfile = profileRequest
        .map(this::saveProfile)
        .orElse(null);

    user.update(
        request.newUsername(),
        request.newEmail(),
        request.newPassword(),
        newProfile
    );

    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException(
            "User with id " + userId + " not found"));

    if (user.getProfile() != null) {
      binaryContentStorage.delete(user.getProfile().getId());
    }

    userRepository.delete(user);
  }



  private BinaryContent saveProfile(BinaryContentCreateRequest request) {

    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );

    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), request.bytes());

    return binaryContent;
  }

  private void validateDuplicate(String username, String email) {

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("User with email " + email + " already exists");
    }

    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("User with username " + username + " already exists");
    }
  }
}