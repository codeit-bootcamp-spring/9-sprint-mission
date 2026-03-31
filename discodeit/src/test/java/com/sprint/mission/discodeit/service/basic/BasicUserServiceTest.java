package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserMapper userMapper;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserStatusService userStatusService;
  @Mock
  private BinaryContentService binaryContentService;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private ChannelRepository channelRepository;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 성공")
  void create_Success() {
    UserCreateRequest request = new UserCreateRequest("minju", "test@test.com", "password123");
    User user = new User("minju", "test@test.com", "encoded", null);

    given(userRepository.existsByEmail(anyString())).willReturn(false);
    given(passwordEncoder.encode(anyString())).willReturn("encoded");
    given(userRepository.save(any(User.class))).willReturn(user);
    given(userMapper.toDto(any(User.class))).willReturn(
        new UserDto(UUID.randomUUID(), "minju", "test@test.com", null, true));
    given(userStatusService.isUserOnline(any())).willReturn(true);

    UserDto result = userService.create(request, null);

    assertThat(result.username()).isEqualTo("minju");
    assertThat(result.online()).isTrue();
    verify(userStatusService).create(any());
  }

  @Test
  @DisplayName("중복 이메일 생성 시 실패")
  void create_Fail_EmailExists() {
    UserCreateRequest request = new UserCreateRequest("minju", "test@test.com", "pw");
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    assertThrows(UserAlreadyExistsException.class, () -> userService.create(request, null));
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void update_Success() {
    UUID userId = UUID.randomUUID();
    User user = new User("old", "old@test.com", "pw", null);
    UserUpdateRequest request = new UserUpdateRequest("new", "new@test.com", "newpassword");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(passwordEncoder.encode(anyString())).willReturn("new-encoded");
    given(userStatusService.isUserOnline(any())).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(
        new UserDto(userId, "new", "new@test.com", null, false));

    UserDto result = userService.update(userId, request, null);

    assertThat(result.username()).isEqualTo("new");
    assertThat(result.online()).isFalse();
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void delete_Success() {
    UUID userId = UUID.randomUUID();
    User user = new User("user", "test@test.com", "pw", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findAllByUserId(userId)).willReturn(List.of());

    userService.delete(userId);

    verify(userRepository).delete(user);
  }

  @Test
  @DisplayName("존재하지 않는 사용자 조회 시 실패")
  void findById_Fail_NotFound() {
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
  }
}