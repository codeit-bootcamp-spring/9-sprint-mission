package com.sprint.mission.discodeit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 성공")
  void userCreateSuccess() {
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");
    User savedUser = new User(request.username(), request.password(), request.email(), null);
    UserDto expectedDto = new UserDto(UUID.randomUUID(), request.username(), request.email(), null, true);

    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    UserDto response = userService.create(request, Optional.empty());

    assertThat(response.email()).isEqualTo("test@naver.com");
    assertThat(response.username()).isEqualTo("test");
  }

  @Test
  @DisplayName("사용자 생성 실패 - email 중복")
  void userCreateDuplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage(ErrorCode.DUPLICATE_USER.getMessage());

    then(userRepository).should(never()).save(any());
  }
  
  @Test
  @DisplayName("사용자 수정 성공")
  void userUpdateSuccess() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com", "newpassword");
    User user = new User("test", "test1234", "test@naver.com", null);

    given(userRepository.findById(id)).willReturn(Optional.of(user));

    userService.update(id, request, Optional.empty());

    assertThat(user.getEmail()).isEqualTo("newEmail@email.com");
    assertThat(user.getUsername()).isEqualTo("new");
    then(userRepository).should().findById(id);
  }

  @Test
  @DisplayName("사용자 수정 실패 - 사용자 존재하지 않음")
  void userUpdateFail() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com", "newpassword");

    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(id, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void userDeleteSuccess() {
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    UserStatus status = mock(UserStatus.class);
    BinaryContent profile = mock(BinaryContent.class);

    given(user.getProfile()).willReturn(profile);
    given(user.getStatus()).willReturn(status);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.delete(userId);

    then(userRepository).should().findById(userId);
    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 사용자 존재하지 않음")
  void userDeleteFail() {
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());

    then(userRepository).should(never()).deleteById(any());
  }
}
