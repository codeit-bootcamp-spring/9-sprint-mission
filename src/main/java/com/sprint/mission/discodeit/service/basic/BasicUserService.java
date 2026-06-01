package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
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
  // BinaryContentStorage 의존 제거 — 리스너가 대신 처리합니다.
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

    // 1. 프로필 이미지 메타 데이터만 DB에 저장, 이벤트는 모아뒀다가 마지막에 발행
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

    // 2. 유저 저장 완료 후 이벤트 발행 (트랜잭션 커밋 시 리스너가 바이너리 저장)
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

    // 1. 새 프로필 이미지 메타 데이터만 DB에 저장
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

    // 2. 유저 수정 완료 후 이벤트 발행 (트랜잭션 커밋 시 리스너가 바이너리 저장)
    optionalProfileCreateRequest.ifPresent(profileRequest ->
        eventPublisher.publishEvent(
            new BinaryContentCreatedEvent(nullableProfile, profileRequest.bytes()))
    );

    log.info("사용자 수정 완료: id={}", userId);
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
