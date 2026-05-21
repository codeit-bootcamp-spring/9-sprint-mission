package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.user.InitialAdminRoleChangeNotAllowedException;
import com.sprint.mission.discodeit.exception.user.SelfRoleChangeNotAllowedException;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

  @InjectMocks
  private BasicUserService userService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("create 성공: 중복이 없으면 사용자를 저장하고 DTO를 반환한다")
  void create_success() {
    UserCreateRequest request = new UserCreateRequest(
        "jun", "jun@test.com", "password123");
    UserResponse expected = new UserResponse(
        UUID.randomUUID(), "jun", "jun@test.com", null, false);

    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(passwordEncoder.encode(request.password())).willReturn("encodedPassword");
    given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));
    given(userMapper.toResponse(any(User.class))).willReturn(expected);

    UserResponse actual = userService.create(request, Optional.empty());

    assertSame(expected, actual);
    then(userRepository).should().existsByEmail(request.email());
    then(userRepository).should().existsByUsername(request.username());
    then(passwordEncoder).should().encode(request.password());
    then(userRepository).should().save(Mockito.argThat(user ->
        "encodedPassword".equals(user.getPassword()) && user.getRole() == UserRole.USER
    ));
    then(userMapper).should().toResponse(any(User.class));
    then(binaryContentRepository).shouldHaveNoInteractions();
    then(binaryContentStorage).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create 실패: 이메일이 중복이면 예외가 발생한다")
  void create_fail_duplicateEmail() {
    UserCreateRequest request = new UserCreateRequest(
        "jun", "jun@test.com", "password123");

    given(userRepository.existsByEmail(request.email())).willReturn(true);

    assertThrows(UserAlreadyExistException.class,
        () -> userService.create(request, Optional.empty()));

    then(userRepository).should().existsByEmail(request.email());
    then(userRepository).shouldHaveNoMoreInteractions();
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("update 성공: 프로필 포함 업데이트 시 사용자 정보가 변경된다")
  void update_success() {
    UUID userId = UUID.randomUUID();
    User user = new User(
        "before", "before@test.com", "oldPassword", null);
    UserUpdateRequest request = new UserUpdateRequest(
        "after", "after@test.com", "newPassword");
    BinaryContentCreateRequest profileRequest = new BinaryContentCreateRequest(
        "profile.png", "image/png", new byte[]{1, 2, 3});

    BinaryContent savedProfile = new BinaryContent(
        "profile.png", 3L, "image/png");
    UUID profileId = UUID.randomUUID();
    ReflectionTestUtils.setField(savedProfile, "id", profileId);

    UserResponse expected = new UserResponse(
        UUID.randomUUID(), "after", "after@test.com", null, false);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(savedProfile);
    given(userMapper.toResponse(user)).willReturn(expected);

    UserResponse actual = userService.update(userId, request, Optional.of(profileRequest));

    assertSame(expected, actual);
    assertEquals("after", user.getUsername());
    assertEquals("after@test.com", user.getEmail());
    assertEquals("newPassword", user.getPassword());
    assertNotNull(user.getProfile());

    then(userRepository).should().findById(userId);
    then(userRepository).should().existsByEmail(request.newEmail());
    then(userRepository).should().existsByUsername(request.newUsername());
    then(binaryContentRepository).should().save(any(BinaryContent.class));
    then(binaryContentStorage).should().put(eq(profileId), eq(profileRequest.bytes()));
    then(userMapper).should().toResponse(user);
  }

  @Test
  @DisplayName("update 실패: 대상 사용자가 없으면 예외가 발생한다")
  void update_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest(
        "after", "after@test.com", "newPassword");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.update(userId, request, Optional.empty()));

    then(userRepository).should().findById(userId);
    then(userRepository).shouldHaveNoMoreInteractions();
    then(binaryContentRepository).shouldHaveNoInteractions();
    then(binaryContentStorage).shouldHaveNoInteractions();
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("updateRole 성공: 사용자 권한을 변경한다")
  void updateRole_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "password123", null);
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.CHANNEL_MANAGER);
    UserResponse expected = new UserResponse(
        userId, "jun", "jun@test.com", null, false, UserRole.CHANNEL_MANAGER);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userMapper.toResponse(user)).willReturn(expected);

    UserResponse actual = userService.updateRole(request);

    assertSame(expected, actual);
    assertEquals(UserRole.CHANNEL_MANAGER, user.getRole());
    then(userRepository).should().findById(userId);
    then(userMapper).should().toResponse(user);
  }

  @Test
  @DisplayName("updateRole 실패: 대상 사용자가 없으면 예외가 발생한다")
  void updateRole_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.ADMIN);

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.updateRole(request));

    then(userRepository).should().findById(userId);
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("updateRole 실패: 자기 자신의 권한은 변경할 수 없다")
  void updateRole_fail_selfRoleChange() {
    UUID userId = UUID.randomUUID();
    User user = new User("admin", "admin@test.com", "password123", UserRole.ADMIN, null);
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.USER);
    UserResponse principalUser = new UserResponse(
        userId, "admin", "admin@test.com", null, true, UserRole.ADMIN);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(principalUser, "encodedPassword");
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(principal, principal.getPassword(),
            principal.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    assertThrows(SelfRoleChangeNotAllowedException.class, () -> userService.updateRole(request));

    assertEquals(UserRole.ADMIN, user.getRole());
    then(userRepository).should().findById(userId);
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("updateRole 실패: 초기 관리자 계정의 권한은 변경할 수 없다")
  void updateRole_fail_initialAdminRoleChange() {
    UUID userId = UUID.randomUUID();
    User user = new User("admin", "admin@discodeit.local", "password123", UserRole.ADMIN, null);
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.USER);
    ReflectionTestUtils.setField(userService, "adminUsername", "admin");
    ReflectionTestUtils.setField(userService, "adminEmail", "admin@discodeit.local");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    assertThrows(InitialAdminRoleChangeNotAllowedException.class,
        () -> userService.updateRole(request));

    assertEquals(UserRole.ADMIN, user.getRole());
    then(userRepository).should().findById(userId);
    then(userMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("delete 성공: 사용자가 존재하면 삭제한다")
  void delete_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "password123", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    userService.delete(userId);

    then(userRepository).should().findById(userId);
    then(userRepository).should().delete(user);
  }

  @Test
  @DisplayName("delete 실패: 대상 사용자가 없으면 예외가 발생한다")
  void delete_fail_userNotFound() {
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.delete(userId));

    then(userRepository).should().findById(userId);
    then(userRepository).shouldHaveNoMoreInteractions();
  }
}
