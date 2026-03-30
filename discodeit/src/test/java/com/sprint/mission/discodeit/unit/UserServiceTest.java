package com.sprint.mission.discodeit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.base.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private BinaryContentRepository binaryContentRepository;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("사용자 생성 성공")
  void userCreateSuccess() {
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");
    User savedUser = new User(request.username(), request.password(), request.email(), null);
    UserDto expectedDto = new UserDto(UUID.randomUUID(), request.username(), request.email(), null, true);

    given(userRepository.save(any(User.class))).willReturn(savedUser);
    given(userMapper.toDto(any(User.class))).willReturn(expectedDto);

    UserDto response = userService.create(request, Optional.empty());

    assertThat(response.email()).isEqualTo("test@naver.com");
    assertThat(response.username()).isEqualTo("test");
  }

  @Test
  @DisplayName("사용자 생성 실패 - email 중복")
  void userCreateDuplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");
    given(userRepository.existsByEmail(anyString())).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage(ErrorCode.DUPLICATE_USER.getMessage());

    then(userRepository).should(never()).save(any());
  }

  @Test
  @DisplayName("사용자 수정 성공")
  void userUpdateSuccess() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com", "newpassword");
    User user = new User("test", "test1234", "test@naver.com", null);

    given(userRepository.findById(id)).willReturn(Optional.of(user));

    userService.update(id, request, Optional.empty());

    assertThat(user.getEmail()).isEqualTo("newEmail@email.com");
    assertThat(user.getUsername()).isEqualTo("new");
    then(userRepository).should().findById(id);
  }

  @Test
  @DisplayName("사용자 수정 실패 - 사용자 존재하지 않음")
  void userUpdateFail() {
    UUID id = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com", "newpassword");

    given(userRepository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(id, request, Optional.empty()))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("사용자 삭제 성공")
  void userDeleteSuccess() {
    BinaryContent profile = new BinaryContent("profile", 1024L, "image/png");
    User user = new User("test", "test1234", "test@naver.com", profile);
    UserStatus status = new UserStatus(user);
    user.updateUserState(status);

    given(userRepository.findById(any())).willReturn(Optional.of(user));

    userService.delete(user.getId());

    then(userRepository).should().deleteById(any());
  }

  @Test
  @DisplayName("사용자 삭제 실패 - 사용자 존재하지 않음")
  void userDeleteFail() {
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());

    then(userRepository).should(never()).deleteById(any());
  }
  
  @Test
  @DisplayName("모든 사용자 조회 성공")
  void findAll_Unit_Success() {
    User user1 = new User("user1", "pass1", "user1@test.com", null);
    User user2 = new User("user2", "pass2", "user2@test.com", null);
    List<User> users = List.of(user1, user2);

    UserDto dto1 = new UserDto(UUID.randomUUID(), "user1", "user1@test.com", null, null);
    UserDto dto2 = new UserDto(UUID.randomUUID(), "user2", "user2@test.com", null, null);

    given(userRepository.findAllWithStatusAndProfile()).willReturn(users);
    given(userMapper.toDto(user1)).willReturn(dto1);
    given(userMapper.toDto(user2)).willReturn(dto2);

    List<UserDto> result = userService.findAll();

    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).username()).isEqualTo("user1");
    assertThat(result.get(1).username()).isEqualTo("user2");

    verify(userRepository, times(1)).findAllWithStatusAndProfile();
  }

}
