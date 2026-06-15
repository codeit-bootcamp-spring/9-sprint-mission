package com.sprint.mission.discodeit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.ArgumentMatchers.any;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.fixture.UserFixture;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)  // Mockito를 JUnit5에서 사용할 수 있게 활성화
class UserServiceTest {

  @InjectMocks  // 테스트 대상 - @Mock으로 만든 객체들을 자동으로 주입해줌
  private BasicUserService userService;

  @Mock  // 가짜 객체 생성 - 실제 DB 연결 없이 동작
  private UserRepository userRepository;
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
  void createUser_success() {
    // given - 테스트 준비: 어떤 상황인지 설정
    UserCreateRequest request = UserFixture.createUserCreateRequest();
    User mockUser = UserFixture.createUser();
    UserDto mockUserDto = UserFixture.createUserDto();

    // 이메일/유저명 중복 없음을 가정
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(false);
    // save 호출 시 mockUser 반환
    given(userRepository.save(any(User.class))).willReturn(mockUser);
    // mapper 변환 결과 설정
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    // when - 실제 테스트할 메소드 실행
    UserDto result = userService.create(request, Optional.empty());

    // then - 결과 검증
    assertThat(result.username()).isEqualTo("testUser");
    assertThat(result.email()).isEqualTo("test@test.com");
    // userRepository.save()가 실제로 한 번 호출됐는지 확인
    then(userRepository).should().save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 - 이메일 중복")
  void createUser_fail_duplicateEmail() {
    // given - 이미 같은 이메일이 존재하는 상황
    UserCreateRequest request = UserFixture.createUserCreateRequest();
    given(userRepository.existsByEmail(request.email())).willReturn(true);  // 이메일 중복!

    // when & then - 예외가 발생하는지 검증
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);

    // save가 호출되지 않았는지 확인 (예외 발생 후 저장하면 안 됨)
    then(userRepository).should().existsByEmail(request.email());
  }

  @Test
  @DisplayName("유저 생성 실패 - 유저명 중복")
  void createUser_fail_duplicateUsername() {
    // given - 이미 같은 유저명이 존재하는 상황
    UserCreateRequest request = UserFixture.createUserCreateRequest();
    given(userRepository.existsByEmail(request.email())).willReturn(false);
    given(userRepository.existsByUsername(request.username())).willReturn(true);  // 유저명 중복!

    // when & then
    assertThatThrownBy(() -> userService.create(request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("유저 수정 성공")
  void updateUser_success() {
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = UserFixture.createUserUpdateRequest();
    User mockUser = UserFixture.createUser();
    UserDto mockUserDto = UserFixture.createUpdatedUserDto();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(false);
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    UserDto result = userService.update(userId, request, Optional.empty());

    assertThat(result.username()).isEqualTo("newUsername");
    assertThat(result.email()).isEqualTo("newEmail@test.com");

    then(userRepository).should().findById(userId);
  }

  @Test
  @DisplayName("유저 수정 실패 - 이메일 중복")
  void updateUser_fail_duplicateEmail() {
    UUID userId = UUID.randomUUID();  // 임의의 UUID 생성
    User mockUser = UserFixture.createUser();
    UserUpdateRequest request = UserFixture.createUserUpdateRequest();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(true);  // 이메일 중복!

    // when & then - 예외가 발생하는지 검증
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
    // save가 호출되지 않았는지 확인 (예외 발생 후 저장하면 안 됨)
    then(userRepository).should().existsByEmail(request.newEmail());
  }

  @Test
  @DisplayName("유저 수정 실패 - 유저명 중복")
  void updateUser_fail_duplicateUsername() {
    // given - 이미 같은 유저명이 존재하는 상황
    UUID userId = UUID.randomUUID();
    User mockUser = UserFixture.createUser();
    UserUpdateRequest request = UserFixture.createUserUpdateRequest();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
    given(userRepository.existsByEmail(request.newEmail())).willReturn(false);
    given(userRepository.existsByUsername(request.newUsername())).willReturn(true);  // 유저명 중복!

    // when & then
    assertThatThrownBy(() -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(UserAlreadyExistsException.class);
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void deleteUser_success() {
    UUID userId = UUID.randomUUID();
    User mockUser = UserFixture.createUser();

    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

    userService.delete(userId);

    then(userRepository).should().findById(userId);
    then(userRepository).should().deleteById(userId);
  }

  @Test
  @DisplayName("유저 삭제 실패 - 유저 없음")
  void deleteUser_fail() {
    UUID userId = UUID.randomUUID();

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(UserNotFoundException.class);
  }
}
