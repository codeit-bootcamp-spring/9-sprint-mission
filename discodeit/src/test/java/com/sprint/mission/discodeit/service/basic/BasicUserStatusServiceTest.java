package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @Mock
  private UserStatusRepository userStatusRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserStatusMapper userStatusMapper;

  @InjectMocks
  private BasicUserStatusService userStatusService;

  private UUID userStatusId;
  private UUID userId;
  private Instant lastActiveAt;
  private User user;
  private UserStatus userStatus;
  private UserStatusDto userStatusDto;

  @BeforeEach
  void setUp() {
    userStatusId = UUID.randomUUID();
    userId = UUID.randomUUID();
    lastActiveAt = Instant.now();

    user = new User("testUser", "test@example.com", "password", null);
    ReflectionTestUtils.setField(user, "id", userId);

    userStatus = new UserStatus(user, lastActiveAt);
    ReflectionTestUtils.setField(userStatus, "id", userStatusId);

    userStatusDto = new UserStatusDto(userStatusId, userId, lastActiveAt);
  }

  @Test
  @DisplayName("사용자 상태 생성 성공")
  void createUserStatus_Success() {
    // given
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);
    given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // 사용자에게 기존 상태가 없어야 함
    ReflectionTestUtils.setField(user, "status", null);

    // when
    UserStatusDto result = userStatusService.create(request);

    // then
    assertThat(result).isEqualTo(userStatusDto);
    verify(userStatusRepository).save(any(UserStatus.class));
  }


  @Test
  @DisplayName("존재하지 않는 사용자에 대한 상태 생성 시도 시 실패")
  void createUserStatus_WithNonExistentUser_ThrowsException() {
    // given
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, lastActiveAt);
    given(userRepository.findById(eq(userId))).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> userStatusService.create(request))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("사용자 상태 조회 성공")
  void findUserStatus_Success() {
    // given
    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.find(userStatusId);

    // then
    assertThat(result).isEqualTo(userStatusDto);
  }


  @Test
  @DisplayName("전체 사용자 상태 목록 조회 성공")
  void findAllUserStatuses_Success() {
    // given
    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    List<UserStatusDto> result = userStatusService.findAll();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(userStatusDto);
  }

  @Test
  @DisplayName("사용자 상태 수정 성공")
  void updateUserStatus_Success() {
    // given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    given(userStatusRepository.findById(eq(userStatusId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.update(userStatusId, request);

    // then
    assertThat(result).isEqualTo(userStatusDto);
  }


  @Test
  @DisplayName("사용자 ID로 상태 수정 성공")
  void updateUserStatusByUserId_Success() {
    // given
    Instant newLastActiveAt = Instant.now().plusSeconds(60);
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);

    given(userStatusRepository.findByUserId(eq(userId))).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(userStatusDto);

    // when
    UserStatusDto result = userStatusService.updateByUserId(userId, request);

    // then
    assertThat(result).isEqualTo(userStatusDto);
  }


  @Test
  @DisplayName("사용자 상태 삭제 성공")
  void deleteUserStatus_Success() {
    // given
    given(userStatusRepository.existsById(eq(userStatusId))).willReturn(true);

    // when
    userStatusService.delete(userStatusId);

    // then
    verify(userStatusRepository).deleteById(eq(userStatusId));
  }


} 