package com.sprint.mission.discodeit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
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
  @DisplayName("loadUserByUsername 성공: DB 사용자로 DiscodeitUserDetails를 생성한다")
  void loadUserByUsername_success() {
    User user = new User("jun", "jun@test.com", "encodedPassword", UserRole.ADMIN, null);
    UserResponse userResponse = new UserResponse(
        UUID.randomUUID(), "jun", "jun@test.com", null, false, UserRole.ADMIN);

    given(userRepository.findByUsername("jun")).willReturn(Optional.of(user));
    given(userMapper.toResponse(user)).willReturn(userResponse);

    DiscodeitUserDetails userDetails = assertInstanceOf(
        DiscodeitUserDetails.class,
        userDetailsService.loadUserByUsername("jun")
    );

    assertSame(userResponse, userDetails.getUserDto());
    assertEquals("encodedPassword", userDetails.getPassword());
    assertEquals("jun", userDetails.getUsername());
    assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    then(userRepository).should().findByUsername("jun");
    then(userMapper).should().toResponse(user);
  }

  @Test
  @DisplayName("loadUserByUsername 실패: 사용자가 없으면 UsernameNotFoundException이 발생한다")
  void loadUserByUsername_fail_userNotFound() {
    given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

    assertThrows(
        UsernameNotFoundException.class,
        () -> userDetailsService.loadUserByUsername("unknown")
    );

    then(userRepository).should().findByUsername("unknown");
    then(userMapper).shouldHaveNoInteractions();
  }
}
