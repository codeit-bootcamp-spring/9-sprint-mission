package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
  private final org.springframework.security.core.session.SessionRegistry sessionRegistry;

  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);

    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw UserAlreadyExistsException.withEmail(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw UserAlreadyExistsException.withUsername(username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
    User user = new User(username, email, encodedPassword, nullableProfile);
    user.updateRole(Role.USER);

    userRepository.save(user);
    return toDtoWithOnlineStatus(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto findByUsername(String username) {
    log.debug("사용자명으로 조회 시작: {}", username);
    return userRepository.findByUsername(username)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withUsername(username));
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
//    UserDto userDto = userRepository.findById(userId)
//        .map(userMapper::toDto)
//        .orElseThrow(() -> UserNotFoundException.withId(userId));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 조회 완료: id={}", userId);
    return toDtoWithOnlineStatus(user);

  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    return userRepository.findAll().stream()
        .map(this::toDtoWithOnlineStatus)
        .toList();
//    List<UserDto> userDtos = userRepository.findAllWithProfileAndStatus()
//        .stream()
//        .map(userMapper::toDto)
//        .toList();
//    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
//    return userDtos;
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          UserNotFoundException exception = UserNotFoundException.withId(userId);
          return exception;
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();

    if (userRepository.existsByEmail(newEmail)) {
      throw UserAlreadyExistsException.withEmail(newEmail);
    }

    if (userRepository.existsByUsername(newUsername)) {
      throw UserAlreadyExistsException.withUsername(newUsername);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    String encodedNewPassword = passwordEncoder.encode(newPassword);

    user.update(newUsername, newEmail, encodedNewPassword, nullableProfile);

    log.info("사용자 수정 완료: id={}", userId);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: id={}", userId);
  }

  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    user.updateRole(request.newRole());
    invalidateUserSessions(user.getId());

    return toDtoWithOnlineStatus(user);
  }

  private UserDto toDtoWithOnlineStatus(User user) {
    boolean isOnline = sessionRegistry.getAllPrincipals().stream()
        .filter(
            principal -> principal instanceof com.sprint.mission.discodeit.security.DiscodeitUserDetails)
        .map(principal -> (com.sprint.mission.discodeit.security.DiscodeitUserDetails) principal)
        .anyMatch(details -> details.getUserDto().id().equals(user.getId()));

    UserDto dto = userMapper.toDto(user);
    return new UserDto(
        dto.id(),
        dto.username(),
        dto.email(),
        dto.profile(),
        isOnline,
        dto.role()
    );
  }

  private void invalidateUserSessions(UUID userId) {
    sessionRegistry.getAllPrincipals().stream()
        .filter(
            principal -> principal instanceof com.sprint.mission.discodeit.security.DiscodeitUserDetails)
        .forEach(principal -> {
          com.sprint.mission.discodeit.security.DiscodeitUserDetails details = (com.sprint.mission.discodeit.security.DiscodeitUserDetails) principal;
          if (details.getUserDto().id().equals(userId)) {
            sessionRegistry.getAllSessions(principal, false)
                .forEach(org.springframework.security.core.session.SessionInformation::expireNow);
          }
        });
  }
}
