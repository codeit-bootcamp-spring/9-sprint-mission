package com.sprint.mission.discodeit.unit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
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
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  // 사용자 생성 성공
  @Test
  void userCreateSuccess(){
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");
    User user = new User(request.username(),request.password(),request.email(), null);
    UserDto dto = new UserDto(UUID.randomUUID(), request.username(), request.email(), null, true);

    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(dto);

    UserDto response = userService.create(request, Optional.empty());

    assertEquals("test@naver.com", response.email());
    assertEquals("test", response.username());
  }

  // email 중복으로 인한 사용자 생성 실패
  @Test
  void userCreateDuplicateEmail(){
    UserCreateRequest request = new UserCreateRequest("test@naver.com", "test", "test1234");

    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
        ()->userService.create(request, Optional.empty()));

    assertEquals(ErrorCode.DUPLICATE_USER.getMessage(), exception.getMessage());
  }

  // 사용자 수정 성공
  @Test
  void userUpdateSuccess(){
    UUID id = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com"
        , "newpassword");
    User user = new User("test", "test1234","test@naver.com", null);
    UserDto dto = new UserDto(id, request.newUsername(), request.newEmail(), null, true);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(userMapper.toDto(any(User.class))).thenReturn(dto);

    UserDto response = userService.update(id , request, Optional.empty());

    assertEquals("newEmail@email.com", response.email());
    assertEquals("new", response.username());

    assertEquals("newEmail@email.com", user.getEmail());
    assertEquals("new", user.getUsername());
  }

  // 사용자 수정 실패
  @Test
  void userUpdateFail(){
    UUID id = UUID.randomUUID();

    UserUpdateRequest request = new UserUpdateRequest("new", "newEmail@email.com"
        , "newpassword");
    User user = new User("test", "test1234","test@naver.com", null);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        ()->userService.update(id, request, Optional.empty()));

    assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());

  }

  // 사용자 삭제 성공
  @Test
  void userDeleteSuccess(){
    UUID userId = UUID.randomUUID();
    UUID statusId = UUID.randomUUID();
    UUID profileId = UUID.randomUUID();

    User user = mock(User.class);
    UserStatus status = mock(UserStatus.class);
    BinaryContent profile = mock(BinaryContent.class);

    when(status.getId()).thenReturn(statusId);
    when(profile.getId()).thenReturn(profileId);
    when(user.getId()).thenReturn(userId);

    when(user.getProfile()).thenReturn(profile);
    when(user.getStatus()).thenReturn(status);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

    userService.delete(userId);

    verify(userRepository).deleteById(userId);
  }

  // 사용자 삭제 실패
  @Test
  void userDeleteFail(){
    UUID userId = UUID.randomUUID();

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class,
        ()->userService.delete(userId));

    assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), exception.getMessage());
  }
}
