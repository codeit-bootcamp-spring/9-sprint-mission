package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sun.jdi.request.DuplicateRequestException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserStatusRepository userStatusRepository;
  @Mock private UserMapper userMapper;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService userService;


  @Test
  void create_success() {

    UserCreateRequest request =
        new UserCreateRequest("test", "test@email.com", "1234");

    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);

    User user = new User("test", "test@email.com", "1234", null);

    UserDto dto = new UserDto(
        UUID.randomUUID(),
        "test",
        "test@email.com",
        null,
        false
    );

    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(dto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result.username()).isEqualTo("test");
    assertThat(result.email()).isEqualTo("test@email.com");
  }

  @Test
  void create_fail_duplicate_email() {

    UserCreateRequest request =
        new UserCreateRequest("test", "dup@email.com", "1234");

    given(userRepository.existsByEmail(request.email())).willReturn(true);

    assertThatThrownBy(() ->
        userService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateRequestException.class);
  }


  @Test
  void update_success() {

    UUID userId = UUID.randomUUID();

    User user = new User("old", "old@email.com", "1234", null);

    UserUpdateRequest request =
        new UserUpdateRequest("new", "new@email.com", "5678");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);

    UserDto dto = new UserDto(
        userId,
        "new",
        "new@email.com",
        null,
        false
    );

    given(userMapper.toDto(user)).willReturn(dto);

    UserDto result = userService.update(userId, request, Optional.empty());

    assertThat(result.username()).isEqualTo("new");
    assertThat(result.email()).isEqualTo("new@email.com");
  }

  @Test
  void update_fail_user_not_found() {

    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    UserUpdateRequest request =
        new UserUpdateRequest("new", "new@email.com", "5678");


    assertThatThrownBy(() ->
        userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }


  @Test
  void delete_success() {

    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(true);

    userService.delete(userId);

    then(userRepository).should().deleteById(userId);
  }

  @Test
  void delete_fail_user_not_found() {

    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(false);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}