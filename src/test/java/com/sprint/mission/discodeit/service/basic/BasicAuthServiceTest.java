package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
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
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        String username = "testuser";
        String password = "password123";
        LoginRequest request = new LoginRequest(username, password);
        User user = new User(username, "test@test.com", password, null);
        UserDto expectedDto = new UserDto(UUID.randomUUID(), username, "test@test.com", null, true);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(userMapper.toDto(user)).willReturn(expectedDto);

        // when
        UserDto result = authService.login(request);

        // then
        assertThat(result.username()).isEqualTo(username);
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음 (보안상 InvalidPasswordException 통일)")
    void login_fail_userNotFound() {
        // given
        LoginRequest request = new LoginRequest("nonexistent", "password123");
        given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalidPassword() {
        // given
        String username = "testuser";
        LoginRequest request = new LoginRequest(username, "wrongpassword");
        User user = new User(username, "test@test.com", "correctpassword", null);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidPasswordException.class);
    }
}
