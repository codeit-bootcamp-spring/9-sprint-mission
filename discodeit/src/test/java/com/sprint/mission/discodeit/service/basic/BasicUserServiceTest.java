package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @InjectMocks
  private BasicUserService userService;

  @Mock private UserRepository userRepository;
  @Mock private UserStatusRepository userStatusRepository;
  @Mock private UserMapper userMapper;
  @Mock private BinaryContentRepository binaryContentRepository;
  @Mock private BinaryContentStorage binaryContentStorage;

  // ==========================================
  // 1. Create 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("유저 생성 성공 (프로필 이미지 없음)")
  void create_Success() {
    // Given
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");
    Optional<BinaryContentCreateRequest> profile = Optional.empty();
    UserDto mockDto = mock(UserDto.class);

    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(mockDto); // 매퍼 동작 가짜로 지정

    // When
    UserDto result = userService.create(request, profile);

    // Then
    assertThat(result).isNotNull();
    then(userRepository).should(times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 (이메일 중복)")
  void create_Fail_DuplicateEmail() {
    // Given
    UserCreateRequest request = new UserCreateRequest("testUser", "test@email.com", "password");
    Optional<BinaryContentCreateRequest> profile = Optional.empty();

    // 이메일이 이미 존재한다고 설정
    given(userRepository.existsByEmail(request.email())).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> userService.create(request, profile))
        .isInstanceOf(UserAlreadyExistsException.class);

    then(userRepository).should(times(0)).save(any(User.class));
  }

  // ==========================================
  // 2. Update 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("유저 수정 성공 (프로필 이미지 없음)")
  void update_Success() {
    // Given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@email.com", "newPass");
    Optional<BinaryContentCreateRequest> profile = Optional.empty();


    User existingUser = mock(User.class);
    UserDto mockDto = mock(UserDto.class);

    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(userMapper.toDto(existingUser)).willReturn(mockDto);

    // When
    UserDto result = userService.update(userId, request, profile);

    // Then
    assertThat(result).isNotNull();
    then(existingUser).should(times(1)).update(request.newUsername(), request.newEmail(), request.newPassword(), null);
  }

  @Test
  @DisplayName("유저 수정 실패 (존재하지 않는 유저)")
  void update_Fail_UserNotFound() {
    // Given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newUser", "new@email.com", "newPass");
    Optional<BinaryContentCreateRequest> profile = Optional.empty();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> userService.update(userId, request, profile))
        .isInstanceOf(UserNotFoundException.class);
  }

  // ==========================================
  // 3. Delete 메소드 테스트
  // ==========================================
  @Test
  @DisplayName("유저 삭제 성공")
  void delete_Success() {
    // Given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    // When
    userService.delete(userId);

    // Then
    then(userRepository).should(times(1)).deleteById(userId);
  }

  @Test
  @DisplayName("유저 삭제 실패 (존재하지 않는 유저)")
  void delete_Fail_UserNotFound() {
    // Given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // When & Then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);

    then(userRepository).should(times(0)).deleteById(any());
  }
}