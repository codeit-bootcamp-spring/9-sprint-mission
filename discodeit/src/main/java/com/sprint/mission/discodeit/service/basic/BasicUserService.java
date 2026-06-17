package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.ErrorDetail;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinaryContentRepository binaryContentRepository;
  private final ApplicationEventPublisher eventPublisher;  // BinaryContentStorage 대신
  private final PasswordEncoder passwordEncoder;

  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    log.info("사용자 생성 로직 시작 - username: {}, email: {}", username, email);

    if (userRepository.existsByEmail(email)) {
      log.warn("사용자 생성 실패: 이미 존재하는 이메일입니다. ({})", email);
      throw new UserException(ErrorCode.DUPLICATE_USER, List.of(new ErrorDetail("email", email)));
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("사용자 생성 실패: 이미 존재하는 유저네임입니다. ({})", username);
      throw new UserException(ErrorCode.DUPLICATE_USER,
          List.of(new ErrorDetail("username", username)));
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          // storage.put() 대신 이벤트 발행
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), profileRequest.bytes())
          );
          return binaryContent;
        })
        .orElse(null);

    String password = passwordEncoder.encode(userCreateRequest.password());
    User user = new User(username, email, password, nullableProfile);
    userRepository.save(user);
    log.info("사용자 생성 및 DB 저장 완료 - username: {}", user.getUsername());
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() ->
            new UserNotFoundException(List.of(new ErrorDetail("userId", userId.toString())))
        );
  }

  @Cacheable(value = "users")
  @Override
  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @CacheEvict(value = "users", allEntries = true)
  @PreAuthorize("principal.userDto.id == #userId or hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {

    log.info("사용자 정보 수정 로직 시작 - 대상 userId: {}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 수정 실패: 존재하지 않는 userId 입니다. ({})", userId);
          return new UserException(ErrorCode.USER_NOT_FOUND,
              List.of(new ErrorDetail("userId", userId.toString())));
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      throw new UserException(ErrorCode.DUPLICATE_USER,
          List.of(new ErrorDetail("email", newEmail)));
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new UserException(ErrorCode.DUPLICATE_USER,
          List.of(new ErrorDetail("username", newUsername)));
    }

    BinaryContent nullableProfile = optionalProfileCreateRequest
        .map(profileRequest -> {
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          binaryContentRepository.save(binaryContent);
          // storage.put() 대신 이벤트 발행
          eventPublisher.publishEvent(
              new BinaryContentCreatedEvent(binaryContent.getId(), profileRequest.bytes())
          );
          return binaryContent;
        })
        .orElse(null);

    String newPassword = passwordEncoder.encode(userUpdateRequest.newPassword());
    user.update(newUsername, newEmail, newPassword, nullableProfile);

    log.info("사용자 정보 수정 완료 - 수정된 username: {}", user.getUsername());
    return userMapper.toDto(user);
  }

  @CacheEvict(value = "users", allEntries = true)
  @PreAuthorize("principal.userDto.id == #userId or hasRole('ADMIN')")
  @Transactional
  @Override
  public void delete(UUID userId) {
    log.info("사용자 삭제 로직 시작 - 대상 userId: {}", userId);
    if (!userRepository.existsById(userId)) {
      log.warn("사용자 삭제 실패: 존재하지 않는 userId 입니다. ({})", userId);
      throw new UserException(ErrorCode.USER_NOT_FOUND,
          List.of(new ErrorDetail("userId", userId.toString())));
    }
    userRepository.deleteById(userId);
    log.info("사용자 삭제 완료 - 삭제된 userId: {}", userId);
  }

  private BinaryContent saveProfileFile(BinaryContentCreateRequest request) {
    BinaryContent binaryContent = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );
    binaryContentRepository.save(binaryContent);
    // storage.put() 대신 이벤트 발행
    eventPublisher.publishEvent(
        new BinaryContentCreatedEvent(binaryContent.getId(), request.bytes())
    );
    return binaryContent;
  }

  @CacheEvict(value = "users", allEntries = true)
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(
            List.of(new ErrorDetail("userId", request.userId().toString()))));
    Role oldRole = user.getRole();       // 바꾸기 전 권한 저장
    user.updateRole(request.newRole());  // 권한 변경
    // 권한 변경 이벤트 발행
    eventPublisher.publishEvent(new RoleUpdatedEvent(user, oldRole, request.newRole()));
    return userMapper.toDto(user);
  }
}