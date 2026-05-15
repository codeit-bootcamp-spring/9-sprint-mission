package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.User.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
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
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final SessionRegistry sessionRegistry;


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
          BinaryContent saved = binaryContentRepository.save(binaryContent);
          binaryContentStorage.put(saved.getId(), bytes);
          log.info("프로필 생성 성공: 프로필ID: {},프로필 크기: {}", binaryContent.getId(),
              binaryContent.getSize());
          return saved;
        })
        .orElse(null);
    String password = passwordEncoder.encode(userCreateRequest.password());

    User user = User.builder()
        .username(username)
        .email(email)
        .password(password)
        .profile(nullableProfileId)
        .role(Role.USER)
        .build();

    userRepository.save(user);
    log.info("유저 생성 성공 - 유저 이름 :{} , 유저ID: {}, 유저 이메일: {} , 프로필 포함 여부: {}", user.getUsername(),
        user.getId(),
        user.getEmail(),
        user.getProfile() != null ? "포함했습니다.(" + user.getProfile().getId() + ")" : "포함하지않았습니다."
    );

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
    return userRepository.findAll().stream()
        .map(user -> {
          UserDto dto = userMapper.toDto(user);

          boolean isOnline = sessionRegistry.getAllPrincipals().stream()
              .filter(principal -> principal instanceof DiscodeitUserDetails)
              .map(principal -> (DiscodeitUserDetails) principal)
              .anyMatch(details -> details.getId().equals(user.getId()));

          return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(), isOnline);

        }).toList();
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
    if (!user.getEmail().equals(newEmail) && newEmail != null) {
      if (userRepository.existsByEmail(newEmail)) {
        log.warn("유저 생성 실패 - 중복 이메일: {}", newEmail);
        throw new EmailAlreadyExistsException(newEmail);
      }
    }
    if (newUsername != null && !user.getUsername().equals(newUsername)) {
      if (userRepository.existsByUsername(newUsername)) {
        log.warn("유저 업데이트 실패 - 중복 이름: {}", newUsername);
        throw new UserAlreadyExistsException(newUsername);
      }
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

    userRepository.deleteById(userId);
    log.info("유저 삭제 완료 :  삭제된 유저 Id: {}", userId);

  }

  @Transactional
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public UserDto updateRole(UserRoleUpdateRequest request) {
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("유저 권한 수정 실패 - 존재하지 않는 유저 Id: {}", request.userId());
          return new UserNotFoundException(request.userId());
        });
    user.updateRole(request.newRole());
    log.info("유저 권한 수정 완료 - 유저ID: {},변경된 권한: {}", user.getId(), user.getRole());
    return userMapper.toDto(user);
  }


}
