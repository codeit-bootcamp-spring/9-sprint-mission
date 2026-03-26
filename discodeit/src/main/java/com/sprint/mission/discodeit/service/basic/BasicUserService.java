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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
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

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();
    log.debug("사용자 생성 시도, username={}, email={}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("이미 존재하는 이메일로 생성 시도: {}", email);
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("이미 존재하는 사용자 이름으로 생성 시도: {}", username);
      throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다: " + username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          log.debug("사용자 프로필 생성, 파일명={}, 크기={}바이트, 타입={}", fileName, bytes.length, contentType);

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          log.info("사용자 프로필 저장 완료, id={}, 파일명={}", binaryContent.getId(), fileName);
          return binaryContent;
        })
        .orElse(null);

    String password = userCreateRequest.password();
    User user = new User(username, email, password, nullableProfile);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);

    userRepository.save(user);
    log.info("사용자 생성 완료, id={}, username={}", user.getId(), username);

    return userMapper.toDto(user);
  }

  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시도, id={}", userId);

    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> {
          log.warn("사용자를 찾을 수 없습니다, id={}", userId);
          return new NoSuchElementException("해당 ID의 사용자가 존재하지 않습니다: " + userId);
        });
  }

  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시도");

    List<UserDto> result = userRepository.findAllWithProfileAndStatus()
        .stream()
        .map(userMapper::toDto)
        .toList();

    log.info("모든 사용자 조회 완료, 조회된 사용자 수={}", result.size());
    return result;
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.debug("사용자 업데이트 시도, id={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("업데이트할 사용자를 찾을 수 없습니다, id={}", userId);
          return new NoSuchElementException("해당 ID의 사용자가 존재하지 않습니다: " + userId);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      log.warn("이미 존재하는 이메일로 업데이트 시도: {}", newEmail);
      throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
      log.warn("이미 존재하는 사용자 이름으로 업데이트 시도: {}", newUsername);
      throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다: " + newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          log.debug("사용자 프로필 업데이트, 파일명={}, 크기={}바이트, 타입={}", fileName, bytes.length, contentType);

          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          log.info("사용자 프로필 저장 완료, id={}, 파일명={}", binaryContent.getId(), fileName);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    log.info("사용자 업데이트 완료, id={}, username={}", userId, newUsername);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시도, id={}", userId);

    if (!userRepository.existsById(userId)) {
      log.error("삭제할 사용자를 찾을 수 없습니다, id={}", userId);
      throw new NoSuchElementException("해당 ID의 사용자가 존재하지 않습니다: " + userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료, id={}", userId);
  }
}