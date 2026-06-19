package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
class BasicReadStatusServiceTest {

  @Mock
  private ReadStatusRepository readStatusRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private ChannelRepository channelRepository;
  @Mock
  private ReadStatusMapper readStatusMapper;

  @InjectMocks
  private BasicReadStatusService readStatusService;

  @Test
  @DisplayName("create 성공: 기존 read status가 있으면 재사용해서 반환한다")
  void create_success_existingReadStatus() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant lastReadAt = Instant.now();

    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    User user = new User("jun", "jun@test.com", "password123", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    ReadStatus readStatus = new ReadStatus(user, channel, lastReadAt);
    ReadStatusResponse expected = new ReadStatusResponse(UUID.randomUUID(), userId, channelId, lastReadAt);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)).willReturn(
        Optional.of(readStatus));
    given(readStatusMapper.toResponse(readStatus)).willReturn(expected);

    ReadStatusResponse actual = readStatusService.create(request);

    assertSame(expected, actual);
    then(readStatusRepository).should().findByUser_IdAndChannel_Id(userId, channelId);
    then(readStatusRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  @DisplayName("create 성공: 기존 read status가 없으면 신규 저장 후 반환한다")
  void create_success_newReadStatus() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    Instant lastReadAt = Instant.now();

    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, lastReadAt);
    User user = new User("jun", "jun@test.com", "password123", null);
    Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
    ReadStatus created = new ReadStatus(user, channel, lastReadAt);
    ReadStatusResponse expected = new ReadStatusResponse(UUID.randomUUID(), userId, channelId, lastReadAt);

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
    given(readStatusRepository.findByUser_IdAndChannel_Id(userId, channelId)).willReturn(Optional.empty());
    given(readStatusRepository.save(any(ReadStatus.class))).willReturn(created);
    given(readStatusMapper.toResponse(created)).willReturn(expected);

    ReadStatusResponse actual = readStatusService.create(request);

    assertSame(expected, actual);
    then(readStatusRepository).should().save(any(ReadStatus.class));
  }

  @Test
  @DisplayName("create 실패: user가 없으면 UserNotFoundException이 발생한다")
  void create_fail_userNotFound() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> readStatusService.create(request));

    then(channelRepository).shouldHaveNoInteractions();
    then(readStatusRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("create 실패: channel이 없으면 ChannelNotFoundException이 발생한다")
  void create_fail_channelNotFound() {
    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId, Instant.now());

    given(userRepository.findById(userId)).willReturn(Optional.of(new User("jun", "jun@test.com", "pw", null)));
    given(channelRepository.findById(channelId)).willReturn(Optional.empty());

    assertThrows(ChannelNotFoundException.class, () -> readStatusService.create(request));

    then(readStatusRepository).shouldHaveNoInteractions();
  }

  @Test
  @DisplayName("find 실패: 데이터가 없으면 READ_STATUS_NOT_FOUND 예외가 발생한다")
  void find_fail_notFound() {
    UUID readStatusId = UUID.randomUUID();
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

    DiscodeitException ex = assertThrows(DiscodeitException.class,
        () -> readStatusService.find(readStatusId));

    assertEquals(ErrorCode.READ_STATUS_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("update 성공: lastReadAt을 수정하고 DTO를 반환한다")
  void update_success() {
    UUID readStatusId = UUID.randomUUID();
    Instant before = Instant.parse("2026-01-01T00:00:00Z");
    Instant after = Instant.parse("2026-01-01T01:00:00Z");

    ReadStatus readStatus = new ReadStatus(
        new User("jun", "jun@test.com", "password123", null),
        new Channel(ChannelType.PUBLIC, "general", "desc"),
        before
    );
    ReadStatusResponse expected = new ReadStatusResponse(
        readStatusId,
        UUID.randomUUID(),
        UUID.randomUUID(),
        after
    );

    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toResponse(readStatus)).willReturn(expected);

    ReadStatusResponse actual = readStatusService.update(readStatusId, new ReadStatusUpdateRequest(after));

    assertSame(expected, actual);
    assertEquals(after, readStatus.getLastReadAt());
  }

  @Test
  @DisplayName("update 성공: 알림 여부를 수정한다")
  void update_success_notificationEnabled() {
    UUID readStatusId = UUID.randomUUID();
    Instant lastReadAt = Instant.parse("2026-01-01T00:00:00Z");
    ReadStatus readStatus = new ReadStatus(
        new User("jun", "jun@test.com", "password123", null),
        new Channel(ChannelType.PUBLIC, "general", "desc"),
        lastReadAt
    );
    ReadStatusResponse expected = new ReadStatusResponse(
        readStatusId,
        UUID.randomUUID(),
        UUID.randomUUID(),
        lastReadAt,
        true
    );

    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.of(readStatus));
    given(readStatusMapper.toResponse(readStatus)).willReturn(expected);

    ReadStatusResponse actual = readStatusService.update(
        readStatusId,
        new ReadStatusUpdateRequest(null, true)
    );

    assertSame(expected, actual);
    assertTrue(readStatus.isNotificationEnabled());
  }

  @Test
  @DisplayName("생성자 성공: 공개 채널 알림 기본값은 false, 비공개 채널 알림 기본값은 true이다")
  void constructor_success_notificationDefault() {
    User user = new User("jun", "jun@test.com", "password123", null);

    ReadStatus publicReadStatus = new ReadStatus(
        user,
        new Channel(ChannelType.PUBLIC, "general", "desc"),
        Instant.now()
    );
    ReadStatus privateReadStatus = new ReadStatus(
        user,
        new Channel(ChannelType.PRIVATE, "private", null),
        Instant.now()
    );

    assertFalse(publicReadStatus.isNotificationEnabled());
    assertTrue(privateReadStatus.isNotificationEnabled());
  }

  @Test
  @DisplayName("findAllByUserId 성공: 목록을 DTO 목록으로 변환한다")
  void findAllByUserId_success() {
    UUID userId = UUID.randomUUID();
    ReadStatus readStatus = new ReadStatus(
        new User("jun", "jun@test.com", "password123", null),
        new Channel(ChannelType.PUBLIC, "general", "desc"),
        Instant.now()
    );
    ReadStatusResponse response = new ReadStatusResponse(UUID.randomUUID(), userId, UUID.randomUUID(),
        Instant.now());

    given(readStatusRepository.findAllByUser_Id(userId)).willReturn(List.of(readStatus));
    given(readStatusMapper.toResponse(readStatus)).willReturn(response);

    List<ReadStatusResponse> result = readStatusService.findAllByUserId(userId);

    assertEquals(List.of(response), result);
  }

  @Test
  @DisplayName("delete 실패: 데이터가 없으면 READ_STATUS_NOT_FOUND 예외가 발생한다")
  void delete_fail_notFound() {
    UUID readStatusId = UUID.randomUUID();
    given(readStatusRepository.findById(readStatusId)).willReturn(Optional.empty());

    DiscodeitException ex = assertThrows(DiscodeitException.class,
        () -> readStatusService.delete(readStatusId));

    assertEquals(ErrorCode.READ_STATUS_NOT_FOUND, ex.getErrorCode());
    then(readStatusRepository).should().findById(readStatusId);
    then(readStatusRepository).shouldHaveNoMoreInteractions();
  }
}
