package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DuplicateUserException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage binaryContentStorage;

  @InjectMocks
  private BasicUserService basicUserService;

  @DisplayName("create - 성공")
  @Test
  void create_Success() {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "test@test.com", "password");
    given(userRepository.existsByEmail("test@test.com")).willReturn(false);
    given(userRepository.existsByUsername("testuser")).willReturn(false);
    
    User savedUser = new User("testuser", "test@test.com", "password", null);
    UserDto expectedDto = new UserDto(savedUser.getId(), "testuser", "test@test.com", null, null);
    
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    // when
    UserDto result = basicUserService.create(request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("testuser");
    then(userRepository).should().save(any(User.class));
  }

  @DisplayName("create - 실패 (이메일 중복)")
  @Test
  void create_Fail_DuplicateEmail() {
    // given
    UserCreateRequest request = new UserCreateRequest("testuser", "dup@test.com", "password");
    given(userRepository.existsByEmail("dup@test.com")).willReturn(true);

    // when & then
    assertThatThrownBy(() -> basicUserService.create(request, Optional.empty()))
        .isInstanceOf(DuplicateUserException.class);
    
    then(userRepository).should(never()).save(any());
  }

  @DisplayName("update - 성공")
  @Test
  void update_Success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newuser", "new@test.com", "newpass");
    User existingUser = new User("olduser", "old@test.com", "oldpass", null);
    
    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("newuser")).willReturn(false);
    
    UserDto expectedDto = new UserDto(userId, "newuser", "new@test.com", null, null);
    given(userMapper.toDto(existingUser)).willReturn(expectedDto);

    // when
    UserDto result = basicUserService.update(userId, request, Optional.empty());

    // then
    assertThat(result.username()).isEqualTo("newuser");
    assertThat(existingUser.getUsername()).isEqualTo("newuser");
  }

  @DisplayName("update - 실패 (존재하지 않는 유저)")
  @Test
  void update_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newuser", "new@test.com", "newpass");
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> basicUserService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @DisplayName("delete - 성공")
  @Test
  void delete_Success() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    // when
    basicUserService.delete(userId);

    // then
    then(userRepository).should().deleteById(userId);
  }

  @DisplayName("delete - 실패 (존재하지 않는 유저)")
  @Test
  void delete_Fail_UserNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // when & then
    assertThatThrownBy(() -> basicUserService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
    
    then(userRepository).should(never()).deleteById(any());
  }
}
