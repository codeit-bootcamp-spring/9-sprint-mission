package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

  @Test
  @DisplayName("create 성공: 기존 상태가 없으면 신규 상태를 저장한다")
  void create_success() {
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    User user = new User("jun", "jun@test.com", "password123", null);
    UserStatus created = new UserStatus(user, now);
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
    UserStatusResponse expected = new UserStatusResponse(UUID.randomUUID(), userId, now);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());
    given(userStatusRepository.save(any(UserStatus.class))).willReturn(created);
    given(userStatusMapper.toResponse(created)).willReturn(expected);

    UserStatusResponse actual = userStatusService.create(request);

    assertSame(expected, actual);
    then(userStatusRepository).should().save(any(UserStatus.class));
  }

  @Test
  @DisplayName("create 실패: user가 없으면 UserNotFoundException이 발생한다")
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UserStatusCreateRequest request = new UserStatusCreateRequest(userId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userStatusService.create(request));

    then(userStatusRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create 실패: 이미 상태가 존재하면 UserStatusAlreadyExistException이 발생한다")
  void create_fail_alreadyExists() {
    UUID userId = UUID.randomUUID();
    User user = new User("jun", "jun@test.com", "password123", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.findByUser_Id(userId)).willReturn(
        Optional.of(new UserStatus(user, Instant.now())));

    assertThrows(UserStatusAlreadyExistException.class,
        () -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())));

    then(userStatusRepository).should().findByUser_Id(userId);
    then(userStatusMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("findAll 성공: 전체 상태를 DTO 목록으로 반환한다")
  void findAll_success() {
    User user = new User("jun", "jun@test.com", "password123", null);
    UserStatus status = new UserStatus(user, Instant.now());
    UserStatusResponse response = new UserStatusResponse(UUID.randomUUID(), UUID.randomUUID(), Instant.now());

    given(userStatusRepository.findAll()).willReturn(List.of(status));
    given(userStatusMapper.toResponse(status)).willReturn(response);

    List<UserStatusResponse> result = userStatusService.findAll();

    assertEquals(List.of(response), result);
  }

  @Test
  @DisplayName("updateByUserId 성공: userId로 상태를 찾아 마지막 활동 시간을 수정한다")
  void updateByUserId_success() {
    UUID userId = UUID.randomUUID();
    Instant before = Instant.parse("2026-01-01T00:00:00Z");
    Instant after = Instant.parse("2026-01-01T02:00:00Z");

    UserStatus status = new UserStatus(new User("jun", "jun@test.com", "password123", null), before);
    UserStatusResponse expected = new UserStatusResponse(UUID.randomUUID(), userId, after);

    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(status));
    given(userStatusMapper.toResponse(status)).willReturn(expected);

    UserStatusResponse actual = userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(after));

    assertSame(expected, actual);
    assertEquals(after, status.getLastActiveAt());
  }

  @Test
  @DisplayName("updateByUserId 실패: userId 대상 상태가 없으면 UserStatusNotFoundException이 발생한다")
  void updateByUserId_fail_notFound() {
    UUID userId = UUID.randomUUID();
    given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());

    assertThrows(UserStatusNotFoundException.class,
        () -> userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now())));

    then(userStatusMapper).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("delete 실패: 상태가 없으면 UserStatusNotFoundException이 발생한다")
  void delete_fail_notFound() {
    UUID userStatusId = UUID.randomUUID();
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThrows(UserStatusNotFoundException.class, () -> userStatusService.delete(userStatusId));

    then(userStatusRepository).should().findById(userStatusId);
    then(userStatusRepository).shouldHaveNoMoreInteractions();
  }
}

