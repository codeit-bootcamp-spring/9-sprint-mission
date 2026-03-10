package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
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
import com.sprint.mission.discodeit.service.BinaryContentService;
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
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentService binaryContentService; // 메타데이터 저장용

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(req -> new BinaryContent(req.fileName(), req.contentType(), req.bytes(), null))
        .orElse(null);

    User user = new User(userCreateRequest.username(), userCreateRequest.email(),
        userCreateRequest.password(), nullableProfile);
    userRepository.save(user);
    UserStatus userStatus = new UserStatus(user, Instant.now());
    userStatusRepository.save(userStatus);

    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public UserDto find(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    try {
      this.updateLastActiveAt(userId);
    } catch (Exception e) {
    }
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

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest request,
      Optional<BinaryContentCreateRequest> optionalProfileRequest) {

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found"));

    BinaryContent profile = optionalProfileRequest
        .map(this::saveBinaryContent)
        .orElse(user.getProfile());

    String newUsername =
        (request.newUsername() != null) ? request.newUsername() : user.getUsername();
    String newEmail = (request.newEmail() != null) ? request.newEmail() : user.getEmail();
    String newPassword =
        (request.newPassword() != null) ? request.newPassword() : user.getPassword();

    user.update(newUsername, newEmail, newPassword, profile);

    return userMapper.toDto(user);
  }

  @Transactional
  public void updateLastActiveAt(UUID userId) {
    UserStatus status = userStatusRepository.findByUser_Id(userId)
        .orElseGet(() -> {
          User user = userRepository.findById(userId)
              .orElseThrow(() -> new NoSuchElementException("User not found"));
          UserStatus newStatus = new UserStatus(user, Instant.now());
          return userStatusRepository.save(newStatus);
        });

    status.update(Instant.now());
    userStatusRepository.save(status);
  }

  private BinaryContent saveBinaryContent(BinaryContentCreateRequest req) {
    // 1. 객체 생성
    BinaryContent content = new BinaryContent(
        req.fileName(),
        req.contentType(),
        req.bytes(),
        null
    );

    // 2. 🚩 저장 (flush 없이 그냥 원래 하던 대로 save만 하세요)
    binaryContentRepository.save(content);

    // 3. 🚩 파일 저장
    binaryContentStorage.put(content.getId(), req.bytes());

    return content;
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new NoSuchElementException("User with id " + userId + " not found");
    }
    userRepository.deleteById(userId);
  }

  @Transactional
  public UserDto updateProfileImage(UUID userId, byte[] imageBytes, String fileName,
      String contentType) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found"));

    BinaryContent newProfile = saveBinaryContent(
        new BinaryContentCreateRequest(fileName, contentType, imageBytes));
    user.update(user.getUsername(), user.getEmail(), user.getPassword(), newProfile);

    return userMapper.toDto(user);
  }
}
