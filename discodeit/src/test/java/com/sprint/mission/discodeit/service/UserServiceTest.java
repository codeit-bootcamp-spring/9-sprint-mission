package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;

  @Test
  void create_success() {
    UserCreateRequest request = new UserCreateRequest(
        "testUser",
        "test@email.com",
        "password123"
    );

    User user = new User(
        "testUser",
        "test@email.com",
        "password123",
        null
    );

    UserDto mockDto = new UserDto(
        UUID.randomUUID(),
        "testUser",
        "test@email.com",
        null,
        true
    );

    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(false);
    given(userRepository.save(any())).willReturn(user);
    given(userMapper.toDto(any())).willReturn(mockDto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result).isNotNull();
    assertThat(result.email()).isEqualTo("test@email.com");

    then(userRepository).should().save(any());
    then(userMapper).should().toDto(any());
  }

  @Test
  void create_fail_duplicate() {
    UserCreateRequest request = new UserCreateRequest(
        "testUser",
        "test@email.com",
        "password123"
    );

    given(userRepository.existsByEmail(any()))
        .willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  void update_success() {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "newUser",
        "new@email.com",
        "newPassword"
    );

    User user = new User(
        "testUser",
        "test@email.com",
        "password123",
        null
    );

    UserDto mockDto = new UserDto(
        userId,
        "newUser",
        "new@email.com",
        null,
        true
    );

    given(userRepository.findById(userId))
        .willReturn(Optional.of(user));

    given(userRepository.existsByEmail(any()))
        .willReturn(false);

    given(userRepository.existsByUsername(any()))
        .willReturn(false);

    given(userMapper.toDto(any()))
        .willReturn(mockDto);

    UserDto result = userService.update(userId, request, Optional.empty());

    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("newUser");

    then(userRepository).should().findById(userId);
    then(userMapper).should().toDto(any());
  }

  @Test
  void update_fail_user_not_found() {
    UUID userId = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest(
        "testUser",
        "test@email.com",
        "password123"
    );

    given(userRepository.findById(userId))
        .willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void delete_success() {
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId))
        .willReturn(true);

    userService.delete(userId);

    then(userRepository).should().deleteById(userId);
  }

  @Test
  void delete_fail_user_not_found() {
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId))
        .willReturn(false);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}