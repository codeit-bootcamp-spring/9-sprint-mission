package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.DiscodeitUserDetails;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.UserUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtRegistry jwtRegistry;
  private final ApplicationEventPublisher eventPublisher;

  @CacheEvict(value = "UserList", key = "'all_users'")
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.warn("유저 생성 실패 - 이메일 {} 은 이미 존재합니다.", email);
      throw new DuplicateEmailException(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("유저 생성 실패 - 이름 {} 은 이미 존재합니다.", username);
      throw new DuplicateNameException(username);
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent content = binaryContentRepository.save(binaryContent);
          log.debug("프로필 사진 저장 - 파일 상세: {}", content);
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
          );
          return content;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(
        username,
        email,
        passwordEncoder.encode(password),
        nullableProfile,
        Role.USER
    );
    Instant now = Instant.now();
    User newUser = userRepository.save(user);

    log.info("유저 생성 및 저장 완료 - 유저: {}", newUser);

    UserDto dto = userMapper.toDto(user);
    eventPublisher.publishEvent(new UserUpdatedEvent("created", dto));

    return dto;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    return userRepository.findWithProfileAndStatusById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }


  @Transactional(readOnly = true)
  @Cacheable(value = "UserList", key = "'all_users'")
  @Override
  public List<UserDto> findAll() {
    return userRepository.findAllWithProfileAndStatusBy()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @PreAuthorize("#userId == authentication.principal.userDto.id or hasRole('ADMIN')")
  @CacheEvict(value = "UserList", key = "'all_users'")
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("유저 검색 실패 - 유저 ID: {}", userId);
          return new UserNotFoundException(userId);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (newEmail != null && !newEmail.equals(user.getEmail())) {
      if (userRepository.existsByEmail(newEmail)) {
        log.warn("유저 업데이트 실패 - 중복된 이메일: {}", newEmail);
        throw new DuplicateEmailException(newEmail);
      }
    }
    if (newUsername != null && !newUsername.equals(user.getUsername())) {
      if (userRepository.existsByUsername(newUsername)) {
        log.warn("유저 업데이트 실패 - 중복된 이름: {}", newUsername);
        throw new DuplicateNameException(newUsername);
      }
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          BinaryContent content = binaryContentRepository.save(binaryContent);
          log.debug("프로필 사진 저장 - 파일 상세: {}", content);
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), bytes)
          );
          return content;
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    log.debug("유저 업데이트 실행 - 유저: {}", user);
    user.update(newUsername, newEmail, passwordEncoder.encode(newPassword), nullableProfile);
    log.info("유저 업데이트 완료 - 유저: {}", user);

    UserDto dto = userMapper.toDto(user);
    eventPublisher.publishEvent(new UserUpdatedEvent("updated", dto));

    return dto;
  }

  @PreAuthorize("#userId == authentication.principal.userDto.id or hasRole('ADMIN')")
  @CacheEvict(value = "UserList", key = "'all_users'")
  @Override
  public void delete(UUID userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

    UserDto dto = userMapper.toDto(user);
    eventPublisher.publishEvent(new UserUpdatedEvent("deleted", dto));

    log.info("유저 삭제 진행 - 유저 ID: {}", userId);

    userRepository.deleteById(userId);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  @CacheEvict(value = "UserList", key = "'all_users'")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));
    Role pastRole = user.getRole();
    Role newRole = request.newRole();
    user.updateRole(newRole);

    // 로그인 상태라면 강제 로그아웃
    if (jwtRegistry.hasActiveJwtInformationByUserId(user.getId())) {
      jwtRegistry.invalidateJwtInformationByUserId(user.getId());
      log.info("권한 변경으로 인한 강제 로그아웃 - 유저 ID: {}", user.getId());
    }

    UserDto dto = userMapper.toDto(user);
    eventPublisher.publishEvent(
        new RoleUpdatedEvent(user, pastRole, newRole)
    );
    log.info("권한 변경 진행 - 유저 ID: {}", user.getId());
    return dto;
  }
}