package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private SessionRegistry sessionRegistry;

  @InjectMocks
  private BasicUserService userService;

  private UUID userId;
  private String username;
  private String email;
  private String password;
  private User user;
  private UserDto userDto;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    username = "testUser";
    email = "test@example.com";
    password = "password123";

    user = new User(username, email, password, null);
    ReflectionTestUtils.setField(user, "id", userId);
    userDto = new UserDto(userId, username, email, null, false);
    lenient().when(sessionRegistry.getAllPrincipals()).thenReturn(List.of());
  }

  @Test
  @DisplayName("사용자 생성 성공")
  void createUser_Success() {
    // given
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    String encodedPassword = "$2a$10$encodedPassword";
    given(userRepository.existsByEmail(eq(email))).willReturn(false);
    given(userRepository.existsByUsername(eq(username))).willReturn(false);
    given(passwordEncoder.encode(password)).willReturn(encodedPassword);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.create(request, Optional.empty());

    // then
    assertThat(result).isEqualTo(userDto);
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    assertThat(userCaptor.getValue().getPassword()).isEqualTo(encodedPassword);
    assertThat(userCaptor.getValue().getRole()).isEqualTo(Role.USER);
    verify(passwordEncoder).encode(password);
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 사용자 생성 시도 시 실패")
  void createUser_WithExistingEmail_ThrowsException() {
    // given
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
    verifyNoInteractions(passwordEncoder);
  }

  @Test
  @DisplayName("이미 존재하는 사용자명으로 사용자 생성 시도 시 실패")
  void createUser_WithExistingUsername_ThrowsException() {
    // given
    UserCreateRequest request = new UserCreateRequest(username, email, password);
    given(userRepository.existsByEmail(eq(email))).willReturn(false);
    given(userRepository.existsByUsername(eq(username))).willReturn(true);

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("사용자 조회 성공")
  void findUser_Success() {
    // given
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.find(userId);

    // then
    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 조회 시 실패")
  void findUser_WithNonExistentId_ThrowsException() {
    // given
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.find(userId))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void updateUser_Success() {
    // given
    String newUsername = "newUsername";
    String newEmail = "new@example.com";
    String newPassword = "newPassword";
    UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(eq(newEmail))).willReturn(false);
    given(userRepository.existsByUsername(eq(newUsername))).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result).isEqualTo(userDto);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 수정 시도 시 실패")
  void updateUser_WithNonExistentId_ThrowsException() {
    // given
    UserUpdateRequest request = new UserUpdateRequest("newUsername", "new@example.com",
        "newPassword");
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 권한 수정 성공")
  void updateRole_Success() {
    UserDto updatedUserDto = new UserDto(userId, username, email, null, false,
        Role.CHANNEL_MANAGER);
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(updatedUserDto);

    UserDto result = userService.updateRole(
        new UserRoleUpdateRequest(userId, Role.CHANNEL_MANAGER));

    assertThat(result).isEqualTo(updatedUserDto);
    assertThat(user.getRole()).isEqualTo(Role.CHANNEL_MANAGER);
  }

  @Test
  @DisplayName("권한 수정 시 로그인된 사용자 세션을 만료한다")
  void updateRole_ExpiresActiveSessions() {
    UserDto principalDto = new UserDto(userId, username, email, null, true, Role.USER);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(principalDto, password);
    SessionInformation sessionInformation =
        new SessionInformation(principal, "session-id", new Date());
    UserDto updatedUserDto = new UserDto(userId, username, email, null, true,
        Role.CHANNEL_MANAGER);

    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userMapper.toDto(user)).willReturn(updatedUserDto);
    given(sessionRegistry.getAllPrincipals()).willReturn(List.of(principal));
    given(sessionRegistry.getAllSessions(principal, false)).willReturn(List.of(sessionInformation));

    UserDto result = userService.updateRole(
        new UserRoleUpdateRequest(userId, Role.CHANNEL_MANAGER));

    assertThat(sessionInformation.isExpired()).isTrue();
    assertThat(result.online()).isTrue();
  }

  @Test
  @DisplayName("존재하지 않는 사용자 권한 수정 실패")
  void updateRole_WithNonExistentId_ThrowsException() {
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.updateRole(
        new UserRoleUpdateRequest(userId, Role.ADMIN)))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void deleteUser_Success() {
    // given
    given(userRepository.existsById(eq(userId))).willReturn(true);

    // when
    userService.delete(userId);

    // then
    verify(userRepository).deleteById(eq(userId));
  }

  @Test
  @DisplayName("존재하지 않는 사용자 삭제 시도 시 실패")
  void deleteUser_WithNonExistentId_ThrowsException() {
    // given
    given(userRepository.existsById(eq(userId))).willReturn(false);

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
} 
