package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final ChannelRepository channelRepository;
  private final UserStatusService userStatusService;
  private final BinaryContentService binaryContentService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public UserDto create(UserCreateRequest request, BinaryContentCreateRequest profileRequest) {
    log.info("Creating new user: email={}", request.email());

    if (userRepository.existsByEmail(request.email())) {
      log.warn("User creation failed: email '{}' already exists", request.email());
      throw new UserAlreadyExistsException(request.email());
    }

    BinaryContent profile = null;
    if (profileRequest != null) {
      var profileDto = binaryContentService.create(profileRequest);
      profile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    String encodedPassword = passwordEncoder.encode(request.password());

    User user = new User(request.username(), request.email(), encodedPassword, profile);
    User savedUser = userRepository.save(user);

    userStatusService.create(savedUser.getId());
    log.info("User created successfully: id={}", savedUser.getId());

    return toDtoWithOnlineStatus(savedUser);
  }

  @Override
  @Transactional
  public UserDto update(UUID id, UserUpdateRequest request,
      BinaryContentCreateRequest profileRequest) {
    log.info("Updating user id: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));

    String encodedPassword = (request.newPassword() != null && !request.newPassword().isBlank())
        ? passwordEncoder.encode(request.newPassword())
        : user.getPassword();

    String updatedUsername = (request.newUsername() != null && !request.newUsername().isBlank())
        ? request.newUsername()
        : user.getUsername();

    String updatedEmail = (request.newEmail() != null && !request.newEmail().isBlank())
        ? request.newEmail()
        : user.getEmail();

    BinaryContent newProfile = user.getProfile();
    if (profileRequest != null) {
      if (user.getProfile() != null) {
        binaryContentService.delete(user.getProfile().getId());
      }
      var profileDto = binaryContentService.create(profileRequest);
      newProfile = binaryContentRepository.findById(profileDto.id()).orElseThrow();
    }

    user.update(updatedUsername, updatedEmail, encodedPassword, newProfile);

    return toDtoWithOnlineStatus(user);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    log.info("Deleting user and cleaning up resources: {}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));

    if (user.getProfile() != null) {
      binaryContentService.delete(user.getProfile().getId());
    }

    channelRepository.findAllByUserId(id).forEach(channel -> channel.removeParticipant(user));

    userRepository.delete(user);
  }

  @Override
  public List<UserDto> findAll() {
    log.debug("Listing all users");
    return userRepository.findAll().stream()
        .map(this::toDtoWithOnlineStatus)
        .toList();
  }

  @Override
  public UserDto findById(UUID id) {
    log.debug("Finding user by id: {}", id);
    return userRepository.findById(id)
        .map(this::toDtoWithOnlineStatus)
        .orElseThrow(() -> new UserNotFoundException(id.toString()));
  }

  private UserDto toDtoWithOnlineStatus(User user) {
    UserDto dto = userMapper.toDto(user);
    return new UserDto(dto.id(), dto.username(), dto.email(), dto.profile(),
        userStatusService.isUserOnline(user.getId()));
  }
}