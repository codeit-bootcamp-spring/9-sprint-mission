package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.JWT.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.sse.SseService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  //
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final SseService sseService;

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 생성 시작: {}", userCreateRequest);
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_EMAIL,Map.of(
          "target", "email",
          "rejectedValue", email
      ));
    }
    if (userRepository.existsByUsername(username)) {
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_USERNAME,Map.of(
          "target", "username",
          "rejectedValue", username
      ));
    }

    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          applicationEventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent, bytes)
          );
          return binaryContent;
        })
        .orElse(null);
    String password = passwordEncoder.encode(userCreateRequest.password());  // 수정

    User user = new User(username, email, password, nullableProfileId);

    userRepository.save(user);

    UserDto userDto = userMapper.toDto(user);
    sseService.broadcast("users.created", userDto);

    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND, Map.of("attemptedUserId", userId)));
    log.info("사용자 조회 완료: id={}", userId);
    return userDto;
  }

  @Cacheable(value = "users")
  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    List<UserDto> userDtos = userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  @PreAuthorize("@userSecurity.isOwner(authentication, #userId)")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);
    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (!userRepository.existsByEmail(newEmail) && userRepository.existsByEmail(newEmail)) {
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_EMAIL,Map.of(
          "target", "email",
          "rejectedValue", newEmail
      ));
    }
    if (!userRepository.existsByUsername(newUsername) && userRepository.existsByUsername(newUsername)) {
      throw new UserAlreadyExistsException(ErrorCode.DUPLICATE_USERNAME,Map.of(
          "target", "username",
          "rejectedValue", newUsername
      ));
    }

    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          applicationEventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent, bytes)
          );
          return binaryContent;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.update(newUsername, newEmail, newPassword, nullableProfileId);
    UserDto userDto = userMapper.toDto(user);
    sseService.broadcast("users.updated", userDto);
    log.info("사용자 수정 완료: id={}", userId);
    return userMapper.toDto(user);
  }

  @CacheEvict(value = "users", allEntries = true)
  @PreAuthorize("@userSecurity.isOwner(authentication, #userId)")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(UserNotFoundException::new);

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: id={}", userId);

    sseService.broadcast("users.deleted", userDto);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public UserDto updateRole(UUID userId, Role newRole) {
    User user = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::new);
    Role oldRole = user.getRole();
    user.updateRole(newRole);

    jwtRegistry.invalidateJwtInformationByUserId(userId);
    applicationEventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));

    UserDto userDto = userMapper.toDto(user);
    sseService.broadcast("users.updated", userDto);

    return userMapper.toDto(user);
  }
}
