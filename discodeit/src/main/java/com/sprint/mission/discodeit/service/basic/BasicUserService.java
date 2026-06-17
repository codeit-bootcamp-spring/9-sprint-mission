package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.SessionService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final AuthService authService;
  private final SessionService sessionService;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @CacheEvict(value = "userDetails", allEntries = true)
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
    String password = passwordEncoder.encode(userCreateRequest.password());

    User user = new User(username, email, password, nullableProfile);
    userRepository.save(user);

    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return userMapper.toDto(user, false);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(value = "userDetail", key = "#userId")
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    boolean isOnline = sessionService.isUserOnline(userId);
    log.info("사용자 조회 완료: id={}", userId);
    return userMapper.toDto(user, isOnline);
  }

  @Transactional(readOnly = true)
  @Override
  @Cacheable(value = "userDetails")
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    Set<UUID> onlineUserIds = sessionService.getOnlineUserIds();
    List<UserDto> userDtos = userRepository.findAllWithProfile()
        .stream()
        .map(user -> {
          boolean isOnline = onlineUserIds.contains(user.getId());
          return userMapper.toDto(user, isOnline);
        })
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "userDetail", key = "#userId"),
      @CacheEvict(value = "userDetails", allEntries = true)
  })
  @PreAuthorize("@securityUtils.isResourceOwner(#userId, principal.username)")
  public UserDto update(@P("userId") UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.withId(userId));

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

    if (userUpdateRequest.newPassword() != null) {
      String newPassword = passwordEncoder.encode(userUpdateRequest.newPassword());
      user.update(newUsername, newEmail, newPassword, nullableProfile);
    } else {
      user.update(newUsername, newEmail, user.getPassword(), nullableProfile);
    }

    log.info("사용자 수정 완료: id={}", userId);
    boolean isOnline = sessionService.isUserOnline(userId);
    return userMapper.toDto(user, isOnline);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "userDetail", key = "#request.userId()"),
      @CacheEvict(value = "userDetails", allEntries = true)
  })
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto update(RoleUpdateRequest request) {
    UUID userId = request.userId();

    log.info("사용자 권한 업데이트 시작: id={}", userId);
    User user = userRepository.findById(userId).orElseThrow(() -> {
      log.warn("존재하지 않는 사용자 id: {}", userId);
      return UserNotFoundException.withId(userId);
    });

    Role oldRole = user.getRole();
    Role newRole = request.newRole();
    user.upateRole(newRole);
    authService.invalidateUserSessionsByUserId(userId);
    log.info("사용자 권한 업데이트 완료. id={}, role={}", userId, newRole);

    eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));

    boolean isOnline = sessionService.isUserOnline(userId);
    return userMapper.toDto(user, isOnline);
  }

  @Override
  @Caching(evict = {
      @CacheEvict(value = "userDetail", key = "#userId"),
      @CacheEvict(value = "userDetails", allEntries = true)
  })
  @PreAuthorize("@securityUtils.isResourceOwner(#userId, principal.username)")
  public void delete(@P("userId") UUID userId) {
    log.debug("사용자 삭제 시작: id={}", userId);

    if (!userRepository.existsById(userId)) {
      throw UserNotFoundException.withId(userId);
    }

    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료: id={}", userId);
  }
}
