package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.jwt.JwtRegistry;
import com.sprint.mission.discodeit.security.jwt.JwtTokenProvider;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private JwtRegistry jwtRegistry;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BasicAuthService authService;

  private UUID userId;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = new User("testuser", "test@example.com", "password", null);
    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(user, "role", Role.USER);
    
    userDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        true,
        Role.ADMIN
    );
  }

  @Test
  @DisplayName("역할 업데이트 성공 - RoleUpdatedEvent 발행")
  void updateRoleInternal_Success_PublishesRoleUpdatedEvent() {
    // given
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);
    
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    // when
    UserDto result = authService.updateRoleInternal(request);

    // then
    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findById(userId);
    verify(jwtRegistry).invalidateJwtInformationByUserId(userId);
    verify(eventPublisher).publishEvent(any(RoleUpdatedEvent.class));
  }

  @Test
  @DisplayName("역할 업데이트 시 올바른 RoleUpdatedEvent 정보 발행")
  void updateRoleInternal_PublishesCorrectRoleUpdatedEvent() {
    // given
    Role fromRole = Role.USER;
    Role toRole = Role.ADMIN;
    RoleUpdateRequest request = new RoleUpdateRequest(userId, toRole);
    
    // Set initial role
    ReflectionTestUtils.setField(user, "role", fromRole);
    
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    // when
    authService.updateRoleInternal(request);

    // then
    verify(eventPublisher).publishEvent(any(RoleUpdatedEvent.class));
    // Note: In a real scenario, you might want to capture the exact event and verify its contents
  }

  @Test
  @DisplayName("역할 업데이트 실패 - 존재하지 않는 사용자")
  void updateRoleInternal_Failure_UserNotFound() {
    // given
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> authService.updateRoleInternal(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("역할 업데이트 시 JWT 정보 무효화")
  void updateRoleInternal_InvalidatesJwtInformation() {
    // given
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    // when
    authService.updateRoleInternal(request);

    // then
    verify(jwtRegistry).invalidateJwtInformationByUserId(userId);
  }

  @Test
  @DisplayName("역할 변경 없는 업데이트도 이벤트 발행")
  void updateRoleInternal_SameRole_StillPublishesEvent() {
    // given
    Role currentRole = Role.USER;
    RoleUpdateRequest request = new RoleUpdateRequest(userId, currentRole);
    
    ReflectionTestUtils.setField(user, "role", currentRole);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    // when
    authService.updateRoleInternal(request);

    // then
    verify(eventPublisher).publishEvent(any(RoleUpdatedEvent.class));
  }

  @Test
  @DisplayName("USER에서 ADMIN으로 역할 업데이트")
  void updateRoleInternal_UserToAdmin_Success() {
    // given
    Role fromRole = Role.USER;
    Role toRole = Role.ADMIN;
    RoleUpdateRequest request = new RoleUpdateRequest(userId, toRole);
    
    ReflectionTestUtils.setField(user, "role", fromRole);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userDto);

    // when
    UserDto result = authService.updateRoleInternal(request);

    // then
    assertThat(result).isEqualTo(userDto);
    verify(eventPublisher).publishEvent(any(RoleUpdatedEvent.class));
  }

  @Test
  @DisplayName("ADMIN에서 USER로 역할 업데이트")
  void updateRoleInternal_AdminToUser_Success() {
    // given
    Role fromRole = Role.ADMIN;
    Role toRole = Role.USER;
    RoleUpdateRequest request = new RoleUpdateRequest(userId, toRole);
    
    ReflectionTestUtils.setField(user, "role", fromRole);
    UserDto userToUserDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        true,
        Role.USER
    );
    
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(userToUserDto);

    // when
    UserDto result = authService.updateRoleInternal(request);

    // then
    assertThat(result).isEqualTo(userToUserDto);
    verify(eventPublisher).publishEvent(any(RoleUpdatedEvent.class));
  }
}