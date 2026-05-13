package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class DiscodeitUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private DiscodeitUserDetailsService userDetailsService;

  @Test
  @DisplayName("사용자 이름으로 DiscodeitUserDetails를 생성한다")
  void loadUserByUsername_Success() {
    String username = "testuser";
    String encodedPassword = "$2a$10$encodedPassword";
    User user = new User(username, "test@example.com", encodedPassword, null);
    UserDto userDto = new UserDto(UUID.randomUUID(), username, "test@example.com", null, true);
    given(userRepository.findByUsernameWithProfile(username)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    DiscodeitUserDetails result = (DiscodeitUserDetails) userDetailsService.loadUserByUsername(
        username);

    assertThat(result.getUserDto()).isEqualTo(userDto);
    assertThat(result.getPassword()).isEqualTo(encodedPassword);
    assertThat(result.getUsername()).isEqualTo(username);
  }

  @Test
  @DisplayName("사용자 이름이 존재하지 않으면 예외를 던진다")
  void loadUserByUsername_UserNotFound() {
    String username = "unknown";
    given(userRepository.findByUsernameWithProfile(username)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
        .isInstanceOf(UsernameNotFoundException.class);
  }
}
