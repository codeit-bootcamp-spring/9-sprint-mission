package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  @Test
  @DisplayName("login 성공: username/password가 일치하면 사용자 정보를 반환한다")
  void login_success() {
    LoginRequest request = new LoginRequest("jun", "password123");
    User user = new User("jun", "jun@test.com", "password123", null);
    UserResponse expected = new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, false);

    given(userRepository.findByUsername(request.username())).willReturn(Optional.of(user));
    given(userMapper.toResponse(user)).willReturn(expected);

    UserResponse actual = authService.login(request);

    assertSame(expected, actual);
    then(userRepository).should().findByUsername(request.username());
    then(userMapper).should().toResponse(user);
  }

  @Test
  @DisplayName("login 실패: username이 없으면 UserNotFoundException이 발생한다")
  void login_fail_userNotFound() {
    LoginRequest request = new LoginRequest("unknown", "password123");

    given(userRepository.findByUsername(request.username())).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> authService.login(request));

    then(userRepository).should().findByUsername(request.username());
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("login 실패: password가 다르면 InvalidPasswordException이 발생한다")
  void login_fail_invalidPassword() {
    LoginRequest request = new LoginRequest("jun", "wrong");
    User user = new User("jun", "jun@test.com", "password123", null);

    given(userRepository.findByUsername(request.username())).willReturn(Optional.of(user));

    assertThrows(InvalidPasswordException.class, () -> authService.login(request));

    then(userRepository).should().findByUsername(request.username());
    then(userMapper).shouldHaveNoInteractions();
  }
}

