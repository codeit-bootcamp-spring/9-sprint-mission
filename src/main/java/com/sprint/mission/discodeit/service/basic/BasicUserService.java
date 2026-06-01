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
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final ApplicationEventPublisher eventPublisher;
  private final PasswordEncoder passwordEncoder;

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
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          return binaryContentRepository.save(binaryContent);
        })
        .orElse(null);

    String encodedPassword = passwordEncoder.encode(userCreateRequest.password());
    User user = new User(username, email, encodedPassword, nullableProfile);
    userRepository.save(user);

    optionalProfileCreateRequest.ifPresent(profileRequest ->
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(nullableProfile, profileRequest.bytes()))
    );

    log.info("사용자 생성 완료: id={}, username={}", user.getId(), username);
    return userMapper.toDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public UserDto find(UUID userId) {
    log.debug("사용자 조회 시작: id={}", userId);
    UserDto userDto = userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> UserNotFoundException.withId(userId));
    log.info("사용자 조회 완료: id={}", userId);
    return userDto;
  }

  @Transactional(readOnly = true)
  @Override
  public List<UserDto> findAll() {
    log.debug("모든 사용자 조회 시작");
    List<UserDto> userDtos = userRepository.findAllWithProfile()
        .stream()
        .map(userMapper::toDto)
        .toList();
    log.info("모든 사용자 조회 완료: 총 {}명", userDtos.size());
    return userDtos;
  }

  @PreAuthorize("principal.userDto.id == #userId")
  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    log.debug("사용자 수정 시작: id={}, request={}", userId, userUpdateRequest);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

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
          BinaryContent binaryContent = new BinaryContent(
              profileRequest.fileName(),
              (long) profileRequest.bytes().length,
              profileRequest.contentType()
          );
          return binaryContentRepository.save(binaryContent);
        })
        .orElse(null);

    String encodedPassword = Optional.ofNullable(userUpdateRequest.newPassword())
        .map(passwordEncoder::encode)
        .orElse(user.getPassword());
    user.update(newUsername, newEmail, encodedPassword, nullableProfile);

    optionalProfileCreateRequest.ifPresent(profileRequest ->
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(nullableProfile, profileRequest.bytes()))
    );

    log.info("사용자 수정 완료: id={}", userId);
    return userMapper.toDto(user);
  }

  // ADMIN만 권한 변경 가능
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  @Override
  public UserDto updateRole(UUID userId, Role newRole) {
    log.debug("권한 변경 시작: userId={}, newRole={}", userId, newRole);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> UserNotFoundException.withId(userId));

    Role oldRole = user.getRole();
    user.updateRole(newRole);

    // 실제로 변경된 경우에만 이벤트 발행
    if (oldRole != newRole) {
      eventPublisher.publishEvent(new RoleUpdatedEvent(userId, oldRole, newRole));
    }

    log.info("권한 변경 완료: userId={}, {} -> {}", userId, oldRole, newRole);
    return userMapper.toDto(user);
  }

  @PreAuthorize("principal.userDto.id == #userId")
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
}
