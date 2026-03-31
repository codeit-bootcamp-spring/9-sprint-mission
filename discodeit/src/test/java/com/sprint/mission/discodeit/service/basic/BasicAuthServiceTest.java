package com.sprint.mission.discodeit.service.basic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicAuthService authService;

  @Test
  void login_Success() {
    LoginRequest request = new LoginRequest("testUser", "password");
    User user = mock(User.class);

    given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));

    given(user.getPassword()).willReturn("encodedPassword");
    given(passwordEncoder.matches("password", "encodedPassword")).willReturn(true);

    given(userMapper.toDto(any())).willReturn(null);

    authService.login(request);

    verify(userRepository).findByUsername("testUser");
  }
}