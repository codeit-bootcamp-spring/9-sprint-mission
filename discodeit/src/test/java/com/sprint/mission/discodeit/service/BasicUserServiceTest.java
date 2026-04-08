package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.junit.jupiter.api.DisplayName;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// 이 클래스에서 Mockito(가짜 객체) 기능을 사용하겠다고 선언합니다.
@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  // @Mock: 진짜 DB와 연결되지 않는 '가짜 창고'를 만듭니다.
  @Mock
  private UserRepository userRepository;

  @Mock
  private com.sprint.mission.discodeit.mapper.UserMapper userMapper;

  // @InjectMocks: 위의 가짜 창고를 주입받아서 사용할 '진짜 서비스'입니다.
  @InjectMocks
  private BasicUserService userService;

  @Test
  @DisplayName("유저 생성 성공 테스트")
  void create_success() {
    // Given 준비 단계
    // 사용자가 가입할 때 보낼 가짜 데이터를 만듭니다.
    UserCreateRequest request = new UserCreateRequest("테스트유저", "test@test.com", "password123");

    // 가짜 창고(Mock)에게 각본을 짜줍니다. "누가 save 하려고 하면 이 데이터를 반환한 척해!"
    // 진짜 객체를 만드는 대신, Mockito를 이용해 껍데기만 있는 '가짜 User'를 만듭니다!
    User mockUser = org.mockito.Mockito.mock(User.class);
    given(userRepository.save(any(User.class))).willReturn(mockUser);

    // 가짜 매퍼에게도 각본을 짜줍니다. "누가 toDto 부르면 이 가짜 DTO를 반환한 척해!"
    UserDto mockUserDto = org.mockito.Mockito.mock(UserDto.class);
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    // When 실행 단계
    // 프로필 사진 없이(Optional.empty()) 가입하는 상황을 진짜로 실행해 봅니다.
    UserDto result = userService.create(request, Optional.empty());

    // Then 검증 단계
    // 결과로 튀어나온 DTO가 null이 아닌지 팩트 체크합니다.
    assertThat(result).isNotNull();

    // 가짜 창고의 save 기능이 실제로 1번 호출되었는지 감시(verify)합니다.
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 테스트 - 이메일 중복")
  void create_fail_duplicated_email() {
    // Given 준비 단계
    UserCreateRequest request = new UserCreateRequest("중복유저", "test@test.com", "password123");

    // 가짜 창고에게 "누가 이메일 중복 검사하면, 이미 가입된 사람 있다고(true) 대답해!" 라고 거짓말 각본을 짭니다.
    given(userRepository.existsByEmail(any())).willReturn(true);

    // When & Then 실행 및 검증 단계
    // 중복된 이메일로 가입(create)을 시도했을 때, 에러가 터지는지(ThrownBy) 검사합니다.
    // 오늘 우리가 만든 UserException (또는 UserAlreadyExistsException)이 터져야 테스트가 통과합니다
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> userService.create(request, Optional.empty()))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }

  @Test
  @DisplayName("유저 수정 성공 테스트")
  void update_success() {
    // Given 준비 단계
    // 수정할 가짜 유저의 ID와 변경할 데이터를 준비합니다.
    java.util.UUID userId = java.util.UUID.randomUUID();
    com.sprint.mission.discodeit.dto.UserUpdateRequest request =
        new com.sprint.mission.discodeit.dto.UserUpdateRequest("수정된이름", "updated@test.com",
            "newPass123");

    // 가짜 창고에게 "누가 ID로 유저 찾으면, 이 가짜 유저를 꺼내줘!"라고 각본을 짭니다.
    User mockUser = org.mockito.Mockito.mock(User.class);
    given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

    // 가짜 매퍼에게도 DTO 변환 각본을 줍니다.
    UserDto mockUserDto = org.mockito.Mockito.mock(UserDto.class);
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    // When 실행 단계
    UserDto result = userService.update(userId, request, Optional.empty());

    // Then 검증 단계
    assertThat(result).isNotNull();
    // 팩트 체크: 수정하기 전에 DB에서 유저를 찾는 로직(findById)이 정말 실행되었는지 감시합니다.
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("유저 수정 실패 테스트 - 존재하지 않는 유저")
  void update_fail_user_not_found() {
    // Given 준비 단계
    java.util.UUID userId = java.util.UUID.randomUUID();
    com.sprint.mission.discodeit.dto.UserUpdateRequest request =
        new com.sprint.mission.discodeit.dto.UserUpdateRequest("수정된이름", "updated@test.com",
            "newPass123");

    // 가짜 창고에게 "누가 유저 찾으면, 텅 빈 상자(Optional.empty)를 줘버려!"라고 지시합니다.
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // When & Then 실행 및 검증 단계
    // 없는 유저를 수정하려고 시도했으니, 아까만든 예외가 무조건 터져야 통과입니다!
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }

  @Test
  @DisplayName("유저 삭제 성공 테스트")
  void delete_success() {
    // Given 준비 단계
    java.util.UUID userId = java.util.UUID.randomUUID();

    // 실제 코드에 맞춰 'existsById'가 무조건 true를 주도록 수정
    given(userRepository.existsById(userId)).willReturn(true);

    // When 실행 단계
    userService.delete(userId);

    // Then 검증 단계
    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("유저 삭제 실패 테스트 - 존재하지 않는 유저")
  void delete_fail_user_not_found() {
    // Given 준비 단계
    java.util.UUID userId = java.util.UUID.randomUUID();

    given(userRepository.existsById(userId)).willReturn(false);

    // When & Then 실행 및 검증 단계
    // 없는 유저를 삭제하려고 했으니, 서비스가 당황하지 않고 예외를 터뜨려야 성공
    org.assertj.core.api.Assertions.assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }
}