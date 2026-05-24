package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
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
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService basicUserService;


  @Test
  @DisplayName("유저 생성 성공 (프로필 없음)")
  void create_Success() {
    UserCreateRequest request = new UserCreateRequest("test@test.com", "tester", "password");
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);

    basicUserService.create(request, Optional.empty());

    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 - 이메일 중복")
  void create_Fail_DuplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("duplicate@test.com", "tester", "password");
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    assertThrows(UserAlreadyExistsException.class,
        () -> basicUserService.create(request, Optional.empty()));

    then(userRepository).should(times(0)).save(any(User.class));
  }

  @Test
  @DisplayName("유저 수정 성공")
  void update_Success() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새로운유저이름", "new@123", "1234");
    User mockUser = new User("username", "email@123", "password", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

    given(userRepository.existsByEmail(any())).willReturn(false);
    given(userRepository.existsByUsername(any())).willReturn(false);

    basicUserService.update(userId, request, Optional.empty());

    assertThat(mockUser.getUsername()).isEqualTo("새로운유저이름");
  }

  @Test
  @DisplayName("유저 수정 실패 - 존재하지 않는 유저")
  void update_Fail_UserNotFound() {
    UUID invalidUserId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새로운유저이름", "new@123", "1234");

    given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());
    assertThrows(UserNotFoundException.class,
        () -> basicUserService.update(invalidUserId, request, Optional.empty()));
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_Success() {
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    basicUserService.delete(userId);

    then(userRepository).should(times(1)).deleteById(userId);
  }

  @Test
  @DisplayName("유저 삭제 실패 - 존재하지 않는 유저")
  void delete_Fail_UserNotFound() {
    UUID invalidUserId = UUID.randomUUID();
    given(userRepository.existsById(invalidUserId)).willReturn(false);

    assertThrows(UserNotFoundException.class, () -> basicUserService.delete(invalidUserId));

    then(userRepository).should(times(0)).deleteById(any());
  }
}