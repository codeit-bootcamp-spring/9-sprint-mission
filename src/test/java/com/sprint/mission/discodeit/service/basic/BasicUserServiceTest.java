package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock UserRepository userRepository;
  @Mock UserMapper userMapper;

  @InjectMocks BasicUserService userService;

  @Test
  void createUser_success() {
    UserCreateRequest request = new UserCreateRequest("user", "test@test.com", "1234");

    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(false);

    userService.create(request, Optional.empty());

    then(userRepository).should().save(any(User.class));
  }

  @Test
  void createUser_fail_emailExists() {
    given(userRepository.existsByEmail(any())).willReturn(true);

    assertThrows(UserAlreadyExistsException.class,
        () -> userService.create(
            new UserCreateRequest("user", "test@test.com", "1234"),
            Optional.empty()));
  }

  @Test
  void createUser_fail_usernameExists() {
    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(true);

    assertThrows(UserAlreadyExistsUsernameException.class,
        () -> userService.create(
            new UserCreateRequest("user", "test@test.com", "1234"),
            Optional.empty()));
  }

  @Test
  void updateUser_success() {
    UUID id = UUID.randomUUID();
    User user = mock(User.class);

    given(userRepository.findById(id)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(false);
    given(userMapper.toDto(any())).willReturn(mock(UserDto.class));

    assertDoesNotThrow(() ->
        userService.update(id, mock(UserUpdateRequest.class), Optional.empty()));
  }

  @Test
  void updateUser_fail_userNotFound() {
    given(userRepository.findById(any())).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.update(UUID.randomUUID(), mock(UserUpdateRequest.class), Optional.empty()));
  }

  @Test
  void updateUser_fail_emailExists() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest updateRequest = new UserUpdateRequest("newUser", "existing@test.com", "12345678");

    given(userRepository.findById(id)).willReturn(Optional.of(mock(User.class)));
    given(userRepository.existsByEmail("existing@test.com")).willReturn(true);

    assertThrows(UserAlreadyExistsException.class,
        () -> userService.update(id, updateRequest, Optional.empty()));
  }

  @Test
  void updateUser_fail_usernameExists() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest updateRequest = new UserUpdateRequest("existingUser", "new@test.com", "12345678");

    given(userRepository.findById(id)).willReturn(Optional.of(mock(User.class)));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("existingUser")).willReturn(true);

    assertThrows(UserAlreadyExistsUsernameException.class,
        () -> userService.update(id, updateRequest, Optional.empty()));
  }

  @Test
  void deleteUser_success() {
    UUID id = UUID.randomUUID();

    given(userRepository.existsById(id)).willReturn(true);

    userService.delete(id);

    then(userRepository).should().deleteById(id);
  }

  @Test
  void deleteUser_fail_userNotFound() {
    given(userRepository.existsById(any())).willReturn(false);

    assertThrows(UserNotFoundException.class,
        () -> userService.delete(UUID.randomUUID()));
  }

  @Test
  void findUser_success() {
    given(userRepository.findById(any()))
        .willReturn(Optional.of(mock(User.class)));
    given(userMapper.toDto(any()))
        .willReturn(mock(UserDto.class));

    assertNotNull(userService.find(UUID.randomUUID()));
  }

  @Test
  void findUser_fail() {
    given(userRepository.findById(any())).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.find(UUID.randomUUID()));
  }
}