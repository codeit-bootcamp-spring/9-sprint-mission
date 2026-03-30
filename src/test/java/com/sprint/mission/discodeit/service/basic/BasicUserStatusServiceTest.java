package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BasicUserStatusServiceTest {

  @Mock
  UserStatusRepository userStatusRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  UserStatusMapper userStatusMapper;

  @InjectMocks
  BasicUserStatusService userStatusService;

  @Test
  void create_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@test.com", "password123", null);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.save(org.mockito.ArgumentMatchers.any(UserStatus.class)))
        .willAnswer(invocation -> invocation.getArgument(0));
    given(userStatusMapper.toDto(org.mockito.ArgumentMatchers.any(UserStatus.class)))
        .willReturn(new UserStatusDto(UUID.randomUUID(), userId, Instant.now()));

    UserStatusDto result = userStatusService.create(new UserStatusCreateRequest(userId, Instant.now()));

    assertNotNull(result);
  }

  @Test
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> userStatusService.create(new UserStatusCreateRequest(userId, Instant.now())));
  }

  @Test
  void updateByUserId_success() {
    UUID userId = UUID.randomUUID();
    User user = new User("alice", "alice@test.com", "password123", null);
    UserStatus userStatus = new UserStatus(user, Instant.now());

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(userStatus));
    given(userStatusMapper.toDto(userStatus)).willReturn(new UserStatusDto(UUID.randomUUID(), userId, Instant.now()));

    UserStatusDto result = userStatusService.updateByUserId(userId, new UserStatusUpdateRequest(Instant.now()));

    assertNotNull(result);
  }

  @Test
  void update_fail_notFound() {
    UUID userStatusId = UUID.randomUUID();
    given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,
        () -> userStatusService.update(userStatusId, new UserStatusUpdateRequest(Instant.now())));
  }

  @Test
  void delete_success() {
    UUID userStatusId = UUID.randomUUID();
    given(userStatusRepository.existsById(userStatusId)).willReturn(true);

    userStatusService.delete(userStatusId);

    then(userStatusRepository).should().deleteById(userStatusId);
  }

  @Test
  void findAll_success() {
    User user = new User("alice", "alice@test.com", "password123", null);
    UserStatus userStatus = new UserStatus(user, Instant.now());

    given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
    given(userStatusMapper.toDto(userStatus)).willReturn(new UserStatusDto(UUID.randomUUID(), UUID.randomUUID(), Instant.now()));

    List<UserStatusDto> result = userStatusService.findAll();

    assertNotNull(result);
  }
}


