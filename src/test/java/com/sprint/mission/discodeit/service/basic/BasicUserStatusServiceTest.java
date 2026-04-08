package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusAlreadyExistsException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.lang.reflect.Field;
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

    private final UUID userId = UUID.randomUUID();
    private final UUID userStatusId = UUID.randomUUID();
    private final Instant now = Instant.now();

    @Test
    @DisplayName("사용자 상태 생성 성공")
    void create_success() {
        // given
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus userStatus = new UserStatus(user, now);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, now);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(userStatus);
        given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

        // when
        UserStatusDto result = userStatusService.create(request);

        // then
        assertThat(result.userId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자 상태 생성 실패 - 사용자 없음")
    void create_fail_userNotFound() {
        // given
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStatusService.create(request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 상태 생성 실패 - 이미 존재")
    void create_fail_alreadyExists() throws Exception {
        // given
        UserStatusCreateRequest request = new UserStatusCreateRequest(userId, now);
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus existingStatus = new UserStatus(user, now);
        Field statusField = User.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(user, existingStatus);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // when & then
        assertThatThrownBy(() -> userStatusService.create(request))
                .isInstanceOf(UserStatusAlreadyExistsException.class);
    }

    @Test
    @DisplayName("사용자 상태 단건 조회 성공")
    void find_success() {
        // given
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus userStatus = new UserStatus(user, now);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, now);

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // when
        UserStatusDto result = userStatusService.find(userStatusId);

        // then
        assertThat(result.id()).isEqualTo(userStatusId);
    }

    @Test
    @DisplayName("사용자 상태 단건 조회 실패 - 없음")
    void find_fail_notFound() {
        // given
        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStatusService.find(userStatusId))
                .isInstanceOf(UserStatusNotFoundException.class);
    }

    @Test
    @DisplayName("전체 사용자 상태 목록 조회")
    void findAll_success() {
        // given
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus userStatus = new UserStatus(user, now);
        UserStatusDto dto = new UserStatusDto(userStatusId, userId, now);

        given(userStatusRepository.findAll()).willReturn(List.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(dto);

        // when
        List<UserStatusDto> result = userStatusService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("사용자 상태 수정 성공")
    void update_success() {
        // given
        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus userStatus = new UserStatus(user, now);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, newLastActiveAt);

        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // when
        UserStatusDto result = userStatusService.update(userStatusId, request);

        // then
        assertThat(result.lastActiveAt()).isEqualTo(newLastActiveAt);
    }

    @Test
    @DisplayName("사용자 상태 수정 실패 - 없음")
    void update_fail_notFound() {
        // given
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
        given(userStatusRepository.findById(userStatusId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStatusService.update(userStatusId, request))
                .isInstanceOf(UserStatusNotFoundException.class);
    }

    @Test
    @DisplayName("userId로 상태 업데이트 성공 - 기존 상태 존재")
    void updateByUserId_success_existing() {
        // given
        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus userStatus = new UserStatus(user, now);
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, newLastActiveAt);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.of(userStatus));
        given(userStatusMapper.toDto(userStatus)).willReturn(expectedDto);

        // when
        UserStatusDto result = userStatusService.updateByUserId(userId, request);

        // then
        assertThat(result.lastActiveAt()).isEqualTo(newLastActiveAt);
    }

    @Test
    @DisplayName("userId로 상태 업데이트 성공 - 상태 신규 생성")
    void updateByUserId_success_createNew() {
        // given
        Instant newLastActiveAt = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(newLastActiveAt);
        User user = new User("user1", "user1@test.com", "password1", null);
        UserStatus newStatus = new UserStatus(user, Instant.now());
        UserStatusDto expectedDto = new UserStatusDto(userStatusId, userId, newLastActiveAt);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(userStatusRepository.findByUser_Id(userId)).willReturn(Optional.empty());
        given(userStatusRepository.save(any(UserStatus.class))).willReturn(newStatus);
        given(userStatusMapper.toDto(any(UserStatus.class))).willReturn(expectedDto);

        // when
        UserStatusDto result = userStatusService.updateByUserId(userId, request);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("userId로 상태 업데이트 실패 - 사용자 없음")
    void updateByUserId_fail_userNotFound() {
        // given
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userStatusService.updateByUserId(userId, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 상태 삭제 성공")
    void delete_success() {
        // given
        given(userStatusRepository.existsById(userStatusId)).willReturn(true);

        // when
        userStatusService.delete(userStatusId);

        // then
        then(userStatusRepository).should().deleteById(userStatusId);
    }

    @Test
    @DisplayName("사용자 상태 삭제 실패 - 없음")
    void delete_fail_notFound() {
        // given
        given(userStatusRepository.existsById(userStatusId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userStatusService.delete(userStatusId))
                .isInstanceOf(UserStatusNotFoundException.class);
    }
}
