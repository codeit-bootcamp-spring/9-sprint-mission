package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.util.Assert.isInstanceOf;


import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.error.UserAlreadyExistsException;
import com.sprint.mission.discodeit.error.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
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

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("유저 생성 성공")
  void userCreateSuccessTest() {

    UserCreateRequest request = new UserCreateRequest("YUK", "YUK@gmail.com", "0000");
    User savedUser = new User("YUK", "YUK@gmail.com", "0000", null);

    //더미 생성 필
    UserDto expectedDto = new UserDto(UUID.randomUUID(), "YUK", "YUK@gmail.com", null, null);

    given(userRepository.existsByEmail(anyString())).willReturn(false);
    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    UserDto result = userService.create(request, Optional.empty());

    assertThat(result.username()).isEqualTo(request.username());
    assertThat(result.email()).isEqualTo(request.email());
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 - 동일한 이메일")
  void userCreateFailTest() {

    UserCreateRequest request = new UserCreateRequest("YUK", "YUK@gmail.com", "0000");

    given(userRepository.existsByEmail("YUK@gmail.com")).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

  }


  @Test
  @DisplayName("유저 정보 수정 성공")
  void userUpdateSuccessTest() {

    UserUpdateRequest request = new UserUpdateRequest("YUK", "YUK@gmail.com", "0000");
    User existingUser = new User("SEON", "SEON@gmail.com", "0000", null);
    UserDto expectedDto = new UserDto(UUID.randomUUID(), "YUK", "YUK@gmail.com", null, null);

    given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(anyString())).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    UserDto result = userService.update(UUID.randomUUID(), request, Optional.empty());

    assertThat(result.username()).isEqualTo(request.newUsername());
    assertThat(result.email()).isEqualTo(request.newEmail());
  }

  @Test
  @DisplayName("유저 정보 수정 실패 - 동일한 이메일")
  void userUpdateFailTest() {
    UserUpdateRequest request = new UserUpdateRequest("YUK", "YUK@gmail.com", "0000");
    User existingUser = new User("SEON", "SEON@gmail.com", "0000", null);

    given(userRepository.findById(any(UUID.class))).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail("YUK@gmail.com")).willReturn(true);

    assertThatThrownBy(() -> userService.update(UUID.randomUUID(), request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }


  @Test
  @DisplayName("유저 삭제 성공")
  void userDeleteSuccessTest() {

    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(false);

    userService.delete(userId);

    verify(userRepository).deleteById(userId);
  }


  @Test
  @DisplayName("유저 삭제 실패 - 유저 조회 실패")
  void userDeleteFailTest() {
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(any())).willReturn(true);

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}