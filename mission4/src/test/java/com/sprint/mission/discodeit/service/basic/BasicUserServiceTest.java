package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.User.EmailAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.User.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.local.BinaryContentStorage;
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
  private UserMapper userMapper;
  @Mock
  private BinaryContentRepository binaryContentRepository;
  @Mock
  private BinaryContentStorage storage;
  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserStatusRepository userStatusRepository;
  @InjectMocks
  private BasicUserService userService;


  @Test
  @DisplayName("이메일이 중복 시 유저생성 실패")
  void failCreateUserWithDuplicateEmail() {
    UserCreateRequest request = new UserCreateRequest("승현", "seung@naver.com", "1234");
    given(userRepository.existsByEmail(request.email())).willReturn(true);
    assertThatThrownBy(() -> userService.create(request, Optional.empty())).isInstanceOf(
        EmailAlreadyExistsException.class);

    verify(userRepository, never()).existsByUsername(anyString());
  }

  @Test
  @DisplayName("유저 이름 중복 시 UserAlreadyExistsException와 함께 유저 생성 실패")
  void failCreateUserWithDuplicateUserName() {
    UserCreateRequest request = new UserCreateRequest("승현", "asd@naver.com", "1234");
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(true);

    assertThatThrownBy(() -> userService.create(request, Optional.empty())).isInstanceOf(
        UserAlreadyExistsException.class);
    verify(userRepository).existsByEmail(anyString());
    verify(userRepository, never()).save(any());
  }


  @Test
  @DisplayName("프로필 미포함 유저 생성 성공")
  void SuccessCreateUserWithoutProfile() {
    UserCreateRequest request = new UserCreateRequest("승현", "seung@naver.com", "1234");
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);

    given(userRepository.save(any(User.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    UUID mockId = UUID.randomUUID();
    UserDto dto = new UserDto(mockId, "승현", "seung@naver.com", null, null);
    given(userMapper.toDto(any(User.class))).willReturn(dto);
    UserDto result = userService.create(request, Optional.empty());

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(mockId);
    assertThat(result.username()).isEqualTo("승현");

    verify(userRepository).save(any(User.class));

  }

  @Test
  @DisplayName("프로필 이미지를 가진 유저 생성 성공")
  void SuccessCreateUserWithProfile() {

    UserCreateRequest request = new UserCreateRequest("승현", "seung@naver.com", "1234");
    BinaryContentCreateRequest mockImage = mock(BinaryContentCreateRequest.class);
    given(mockImage.bytes()).willReturn(new byte[1024]);
    BinaryContent mockBinarycontent = mock(BinaryContent.class);
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    given(binaryContentRepository.save(any(BinaryContent.class))).willReturn(mockBinarycontent);
    UUID contentId = UUID.randomUUID();
    BinaryContentDto mockDto = new BinaryContentDto(contentId, "proflie.png", 1024L, "image/png");

    UUID mockId = UUID.randomUUID();
    UserDto dto = new UserDto(mockId, "승현", "seung@naver.com", mockDto, null);
    given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));
    given(userMapper.toDto(any(User.class))).willReturn(dto);

    UserDto result = userService.create(request, Optional.of(mockImage));

    assertThat(result).isNotNull();
    assertThat(result.profile()).isNotNull();
    assertThat(result.profile().id()).isEqualTo(contentId);

    verify(binaryContentRepository).save(any());

  }

  @Test
  @DisplayName("중복 이메일 입력시 EmailAlreadyExistsException와 함께 유저 수정 실패")
  void FailUpdateUserWithValidateEmail() {
    String username = "승현";
    String email = "seung@naver.com";
    String password = "1234";
    User existingUser = new User(username, email, password, null);
    String newUsername = "팝콘";
    String newEmail = "email@email.com";
    String newPassword = "1234123";
    UserUpdateRequest request = new UserUpdateRequest(newUsername, newEmail, newPassword);
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
    given(userRepository.existsByEmail(newEmail)).willReturn(true);
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty())).isInstanceOf(
        EmailAlreadyExistsException.class);

    verify(userRepository, never()).findByUsername(anyString());

  }

  @Test
  @DisplayName("프로필이 없는 유저 업데이트 성공")
  void successUpdateUserWithoutProfile() {
    User user = new User("승현", "seung@naver.com", "1234123", null);
    UserUpdateRequest request = new UserUpdateRequest("corn", "corn@gmail.com", "1234");
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.existsByEmail("corn@gmail.com")).willReturn(false);
    given(userRepository.existsByUsername("corn")).willReturn(false);

    UserDto dto = new UserDto(userId, "corn", "corn@gmail.com", null, null);
    given(userMapper.toDto(any(User.class))).willReturn(dto);
    UserDto result = userService.update(userId, request, Optional.empty());

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(userId);
    assertThat(result.username()).isEqualTo("corn");

  }


  @Test
  @DisplayName("존재하지 않는 userId로 인해 유저 삭제 실패")
  void fail_Delete_User_With_NotExist_UserId() {
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);
    assertThatThrownBy(() -> userService.delete(userId)).isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void successDeleteUser() {
    UUID userId = UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);
    userService.delete(userId);
  }
}