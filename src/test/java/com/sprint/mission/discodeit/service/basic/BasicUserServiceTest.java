package com.sprint.mission.discodeit.service.basic;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.domain.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.domain.UserNotFoundException;
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

  @InjectMocks
  private BasicUserService userService;  // 테스트할 실제 클래스

  @Mock
  private UserRepository userRepository;  // 가짜 Repository

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private BinaryContentStorage binaryContentStorage;

  @Test
  @DisplayName("유저 생성 성공")
  void create_success() {
    // given
    UserCreateRequest request = new UserCreateRequest("홍길동", "test@test.com", "password123");

    given(userRepository.existsByEmail("test@test.com")).willReturn(false);
    given(userRepository.existsByUsername("홍길동")).willReturn(false);

    User user = new User("홍길동", "test@test.com", "password123", null);
    given(userRepository.save(any())).willReturn(user);

    UserDto userDto = new UserDto(user.getId(), "홍길동", "test@test.com", null, false);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.create(request, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("홍길동");
    assertThat(result.email()).isEqualTo("test@test.com");
  }

  @Test
  @DisplayName("유저 생성 실패 - 이메일 중복")
  void create_fail_duplicateEmail() {
    // given
    UserCreateRequest request = new UserCreateRequest("홍길동", "test@test.com", "password123");

    given(userRepository.existsByEmail("test@test.com")).willReturn(true); // 이미 존재

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistException.class);
  }

  @Test
  @DisplayName("유저 수정 성공")
  void update_success() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새이름", "new@test.com", "newpassword123");

    User user = new User("홍길동", "test@test.com", "password123", null);
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("new@test.com")).willReturn(false);
    given(userRepository.existsByUsername("새이름")).willReturn(false);

    UserDto userDto = new UserDto(userId, "새이름", "new@test.com", null, false);
    given(userMapper.toDto(any(User.class))).willReturn(userDto);

    // when
    UserDto result = userService.update(userId, request, Optional.empty());

    // then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("새이름");
    assertThat(result.email()).isEqualTo("new@test.com");
  }

  @Test
  @DisplayName("유저 수정 실패 - 유저 없음")
  void update_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("새이름", "new@test.com", "newpassword123");

    given(userRepository.findById(userId)).willReturn(Optional.empty()); // 유저 없음

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_success() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(true); // 유저 존재

    // when
    userService.delete(userId);

    // then
    then(userRepository).should().deleteById(userId); // deleteById가 실제로 호출됐는지 검증
  }

  @Test
  @DisplayName("유저 삭제 실패 - 유저 없음")
  void delete_fail_userNotFound() {
    // given
    UUID userId = UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(false); // 유저 없음

    // when & then
    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
