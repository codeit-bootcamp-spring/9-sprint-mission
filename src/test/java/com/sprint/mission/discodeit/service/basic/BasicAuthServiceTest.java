package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserInvalidPasswordException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  UserMapper userMapper;

  @InjectMocks
  BasicAuthService authService;

  @Test
  void login_success() {
    User user = new User("alice", "alice@test.com", "password123", null);
    given(userRepository.findByUsername("alice")).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(new UserDto(UUID.randomUUID(), "alice", "alice@test.com", null, true));

    authService.login(new LoginRequest("alice", "password123"));

    then(userMapper).should().toDto(user);
  }

  @Test
  void login_fail_userNotFound() {
    given(userRepository.findByUsername("ghost")).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> authService.login(new LoginRequest("ghost", "password123")));
  }

  @Test
  void login_fail_wrongPassword() {
    User user = new User("alice", "alice@test.com", "password123", null);
    given(userRepository.findByUsername("alice")).willReturn(Optional.of(user));

    assertThrows(UserInvalidPasswordException.class,
        () -> authService.login(new LoginRequest("alice", "wrong-password")));
  }
}

