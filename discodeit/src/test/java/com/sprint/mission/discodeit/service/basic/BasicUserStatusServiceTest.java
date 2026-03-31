package com.sprint.mission.discodeit.service.basic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Collections;
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
  @DisplayName("UserStatus 생성 테스트")
  void create_Success() {
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);
    UserStatus status = new UserStatus();

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userStatusRepository.save(any())).willReturn(status);

    userStatusService.create(userId);

    verify(userStatusRepository).save(any());
  }


  @Test
  @DisplayName("UserStatus 수정 테스트")
  void updateByUserId_Success() {
    UUID userId = UUID.randomUUID();
    UserStatusUpdateRequest req = mock(UserStatusUpdateRequest.class);
    UserStatus status = mock(UserStatus.class);
    UserStatusDto res = mock(UserStatusDto.class);

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(status));
    given(userStatusMapper.toDto(status)).willReturn(res);

    userStatusService.updateByUserId(userId, req);

    verify(userStatusRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("UserStatus 단건 조회 테스트")
  void findByUserId_Success() {
    UUID userId = UUID.randomUUID();
    UserStatus status = mock(UserStatus.class);
    UserStatusDto dto = mock(UserStatusDto.class);

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(status));
    given(userStatusMapper.toDto(status)).willReturn(dto);

    userStatusService.findByUserId(userId);

    verify(userStatusMapper).toDto(any());
  }

  @Test
  @DisplayName("UserStatus 전체 조회 테스트")
  void findAll_Success() {
    UserStatus status = mock(UserStatus.class);
    given(userStatusRepository.findAll()).willReturn(Collections.singletonList(status));
    given(userStatusMapper.toDto(status)).willReturn(mock(UserStatusDto.class));

    userStatusService.findAll();

    verify(userStatusRepository).findAll();
  }

  @Test
  @DisplayName("UserStatus 삭제 테스트")
  void deleteByUserId_Success() {
    UUID userId = UUID.randomUUID();
    UserStatus status = mock(UserStatus.class);

    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(status));

    userStatusService.deleteByUserId(userId);

    verify(userStatusRepository).delete(any());
  }

  @Test
  @DisplayName("UserStatus 온라인 여부 확인 테스트")
  void isUserOnline_Success() {
    UUID userId = UUID.randomUUID();
    given(userStatusRepository.findByUserId(userId)).willReturn(Optional.of(new UserStatus()));

    userStatusService.isUserOnline(userId);

    verify(userStatusRepository).findByUserId(userId);
  }
}