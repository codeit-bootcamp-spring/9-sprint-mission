package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.User.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.local.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final UserMapper userMapper;
  private final MessageRepository messageRepository;


  @Transactional
  @Override
  public UserDto create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    if (userRepository.existsByEmail(email)) {
      log.warn("유저 생성 실패 - 중복 이메일: {}", email);
      throw new EmailAlreadyExistsException(email);
    }
    if (userRepository.existsByUsername(username)) {
      log.warn("유저 생성 실패 - 중복 이름: {}", username);
      throw new UserAlreadyExistsException(username);
    }

    BinaryContent nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType);
          binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(binaryContent.getId(), bytes);
          log.info("프로필 생성 성공: 프로필ID: {},프로필 크기: {}", binaryContent.getId(),
              binaryContent.getSize());
          return binaryContent;
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfileId);
    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(user, now);
    userRepository.save(user);
    log.info("유저 생성 성공 - 유저 이름 :{} , 유저ID: {}, 유저 이메일: {} , 프로필 포함 여부: {}", user.getUsername(),
        user.getId(),
        user.getEmail(),
        user.getProfile() != null ? "포함했습니다.(" + user.getProfile().getId() + ")" : "포함하지않았습니다."
    );
//    userStatusRepository.save(userStatus);
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto find(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toDto)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  ;

  @Override

  @Transactional(readOnly = true)
  public List<UserDto> findAll() {
    return userRepository.findAll()
        .stream()
        .map(userMapper::toDto)
        .toList();
  }

  @Transactional
  @Override
  public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() ->
        {
          log.warn("유저 생성 실패-존재하지 않는 유저 Id:{}", userId);
          return new UserNotFoundException(userId);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(
        newEmail)) {
      log.warn("유저 생성 실패 - 중복 이메일: {}", newEmail);
      throw new EmailAlreadyExistsException(newEmail);

    }
    userRepository.findByUsername(newUsername)
        .filter(existing -> !existing.getId().equals(userId))
        .ifPresent(existing -> {
          log.warn("유저 업데이트 실패 - 중복 이름: {}", newUsername);
          throw new UserAlreadyExistsException(newUsername);
        });

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
    user.update(newUsername, newEmail, newPassword, nullableProfile);
    log.info("유저 업데이트 성공! - 유저 새이름: {},유저 새이메일: {} ,유저 새프로필:{}",
        newUsername, newEmail, nullableProfile);
    return userMapper.toDto(user);
  }

  @Transactional
  @Override
  public void delete(UUID userId) {
    if (!userRepository.existsById(userId)) {
      log.warn("유저 삭제 실패-존재하지 않는 userId:{}", userId);
      throw new UserNotFoundException(userId);
    }
    messageRepository.deleteByUserId(userId);
    userStatusRepository.deleteByUserId(userId);
    userRepository.deleteById(userId);
    log.info("유저 삭제 완료 :  삭제된 유저 Id: {}", userId);

  }


}
