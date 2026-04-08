package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현체.
 * 사용자 CRUD 및 프로필 이미지 관리를 담당한다.
 * 사용자 생성 시 UserStatus도 함께 생성하며, flush/refresh로 1차 캐시를 갱신한다.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;
  private final EntityManager entityManager;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.info("사용자 생성 요청: username={}, email={}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("이메일 중복: email={}", email);
      throw new UserAlreadyExistsException(Map.of("email", email));
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("사용자명 중복: username={}", username);
      throw new UserAlreadyExistsException(Map.of("username", username));
    }

    BinaryContent profile = optionalProfileCreateRequest
        .map(req -> {
          BinaryContent bc = new BinaryContent(req.fileName(), (long) req.bytes().length,
              req.contentType());
          bc = binaryContentRepository.save(bc);
          binaryContentStorage.put(bc.getId(), req.bytes());
          return bc;
        })
        .orElse(null);

    User user = new User(username, email, userCreateRequest.password(), profile);
    user = userRepository.save(user);
    userStatusRepository.save(new UserStatus(user, Instant.now()));

    // JPA 1차 캐시 무효화 후 UserStatus가 로드된 user를 재조회
    entityManager.flush();
    entityManager.refresh(user);

    log.debug("사용자 생성 완료: userId={}", user.getId());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAllWithDetails().stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    log.info("사용자 수정 요청: userId={}", userId);

    if (newEmail != null && !newEmail.equals(user.getEmail())
        && userRepository.existsByEmail(newEmail)) {
      throw new UserAlreadyExistsException(Map.of("email", newEmail));
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())
        && userRepository.existsByUsername(newUsername)) {
      throw new UserAlreadyExistsException(Map.of("username", newUsername));
    }

    BinaryContent newProfile = optionalProfileCreateRequest
        .map(req -> {
          if (user.getProfile() != null) {
            UUID oldProfileId = user.getProfile().getId();
            user.clearProfile();
            binaryContentRepository.flush();
            binaryContentRepository.deleteById(oldProfileId);
            binaryContentStorage.delete(oldProfileId);
          }
          BinaryContent bc = new BinaryContent(req.fileName(), (long) req.bytes().length,
              req.contentType());
          bc = binaryContentRepository.save(bc);
          binaryContentStorage.put(bc.getId(), req.bytes());
          return bc;
        })
        .orElse(null);

    user.update(newUsername, newEmail, userUpdateRequest.newPassword(), newProfile);

    log.debug("사용자 수정 완료: userId={}", userId);
    return userMapper.toDto(user);
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(Map.of("userId", userId)));

    if (user.getProfile() != null) {
      UUID profileId = user.getProfile().getId();
      user.clearProfile();
      binaryContentRepository.flush();
      binaryContentRepository.deleteById(profileId);
      binaryContentStorage.delete(profileId);
    }

    userRepository.delete(user);
    log.info("사용자 삭제 완료: userId={}", userId);
  }
}
