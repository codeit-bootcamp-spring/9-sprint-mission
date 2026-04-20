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

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private com.sprint.mission.discodeit.mapper.UserMapper userMapper;

  @InjectMocks
  private BasicUserService userService;


  // 멘토님 피드백 반영: 재사용 가능한 테스트용 Fixture 객체 생성
  private final User userFixture = new User("샘플유저", "sample@test.com", "password123", null);

  @Test
  @DisplayName("유저 생성 성공 테스트")
  void create_success() {
    // Given
    UserCreateRequest request = new UserCreateRequest("샘플유저", "sample@test.com", "password123");

    // 수정: Mock(가짜 객체) 대신 userFixture(진짜 샘플 객체)를 반환하도록 변경
    given(userRepository.save(any(User.class))).willReturn(userFixture);

    UserDto mockUserDto = org.mockito.Mockito.mock(UserDto.class);
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    // When
    UserDto result = userService.create(request, Optional.empty());

    // Then
    assertThat(result).isNotNull();
    verify(userRepository).save(any(User.class));
  }

  @Test
  @DisplayName("유저 생성 실패 테스트 - 이메일 중복")
  void create_fail_duplicated_email() {
    // Given
    UserCreateRequest request = new UserCreateRequest("중복유저", "test@test.com", "password123");

    given(userRepository.existsByEmail(any())).willReturn(true);

    // When & Then
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> userService.create(request, Optional.empty()))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }

  @Test
  @DisplayName("유저 수정 성공 테스트")
  void update_success() {
    // Given
    java.util.UUID userId = java.util.UUID.randomUUID();
    com.sprint.mission.discodeit.dto.UserUpdateRequest request =
        new com.sprint.mission.discodeit.dto.UserUpdateRequest("수정된이름", "updated@test.com",
            "newPass123");

    // Mock 대신 userFixture 사용!
    given(userRepository.findById(userId)).willReturn(Optional.of(userFixture));

    UserDto mockUserDto = org.mockito.Mockito.mock(UserDto.class);
    given(userMapper.toDto(any(User.class))).willReturn(mockUserDto);

    // When
    UserDto result = userService.update(userId, request, Optional.empty());

    // Then
    assertThat(result).isNotNull();
    verify(userRepository).findById(userId);
  }

  @Test
  @DisplayName("유저 수정 실패 테스트 - 존재하지 않는 유저")
  void update_fail_user_not_found() {
    // Given
    java.util.UUID userId = java.util.UUID.randomUUID();
    com.sprint.mission.discodeit.dto.UserUpdateRequest request =
        new com.sprint.mission.discodeit.dto.UserUpdateRequest("수정된이름", "updated@test.com",
            "newPass123");

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    // When & Then
    org.assertj.core.api.Assertions.assertThatThrownBy(
            () -> userService.update(userId, request, Optional.empty()))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }

  @Test
  @DisplayName("유저 삭제 성공 테스트")
  void delete_success() {
    // Given
    java.util.UUID userId = java.util.UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(true);

    // When
    userService.delete(userId);

    // Then
    verify(userRepository).deleteById(userId);
  }

  @Test
  @DisplayName("유저 삭제 실패 테스트 - 존재하지 않는 유저")
  void delete_fail_user_not_found() {
    // Given
    java.util.UUID userId = java.util.UUID.randomUUID();
    given(userRepository.existsById(userId)).willReturn(false);

    // When & Then
    org.assertj.core.api.Assertions.assertThatThrownBy(() -> userService.delete(userId))
        .isInstanceOf(com.sprint.mission.discodeit.exception.UserException.class);
  }
}