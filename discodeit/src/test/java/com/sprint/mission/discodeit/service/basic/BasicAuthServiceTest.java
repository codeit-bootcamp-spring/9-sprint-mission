package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.InvalidCredentialsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private BasicAuthService authService;

  private UUID userId;
  private String username;
  private String rawPassword;
  private String encodedPassword;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    username = "testUser";
    rawPassword = "Password1!";
    encodedPassword = "$2a$10$encodedPassword";
    user = new User(username, "test@example.com", encodedPassword, null);
    ReflectionTestUtils.setField(user, "id", userId);
    userDto = new UserDto(userId, username, "test@example.com", null, true);
  }

  @Test
  @DisplayName("로그인 성공 시 PasswordEncoder로 비밀번호를 검증한다")
  void login_Success_WithPasswordEncoder() {
    LoginRequest request = new LoginRequest(username, rawPassword);
    given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);
    given(userMapper.toDto(user)).willReturn(userDto);

    UserDto result = authService.login(request);

    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("로그인 실패 - 존재하지 않는 사용자")
  void login_Failure_UserNotFound() {
    LoginRequest request = new LoginRequest(username, rawPassword);
    given(userRepository.findByUsername(username)).willReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치")
  void login_Failure_InvalidCredentials() {
    LoginRequest request = new LoginRequest(username, rawPassword);
    given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
    given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(false);

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(InvalidCredentialsException.class);
  }
}
