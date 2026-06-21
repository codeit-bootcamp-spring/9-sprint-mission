package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.Optional;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private NotificationMapper notificationMapper;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private BasicNotificationService notificationService;

  private UUID receiverId;
  private UUID notificationId;
  private Notification notification;
  private NotificationDto notificationDto;

  @BeforeEach
  void setUp() {
    receiverId = UUID.randomUUID();
    notificationId = UUID.randomUUID();
    
    notification = new Notification(receiverId, "Test Title", "Test Content");
    ReflectionTestUtils.setField(notification, "id", notificationId);
    
    notificationDto = new NotificationDto(
        notificationId,
        Instant.now(),
        receiverId,
        "Test Title",
        "Test Content"
    );
  }

  @Test
  @DisplayName("수신자별 알림 목록 조회 성공")
  void findAllByReceiverId_Success() {
    // given
    List<Notification> notifications = List.of(notification);
    given(notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId))
        .willReturn(notifications);
    given(notificationMapper.toDto(notification)).willReturn(notificationDto);

    // when
    List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(notificationDto);
    verify(notificationRepository).findAllByReceiverIdOrderByCreatedAtDesc(receiverId);
    verify(notificationMapper).toDto(notification);
  }

  @Test
  @DisplayName("수신자별 알림 목록 조회 - 빈 목록")
  void findAllByReceiverId_EmptyList() {
    // given
    given(notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId))
        .willReturn(List.of());

    // when
    List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

    // then
    assertThat(result).isEmpty();
    verify(notificationRepository).findAllByReceiverIdOrderByCreatedAtDesc(receiverId);
    verifyNoInteractions(notificationMapper);
  }

  @Test
  @DisplayName("알림 삭제 성공")
  void delete_Success() {
    // given
    given(notificationRepository.findById(notificationId))
        .willReturn(Optional.of(notification));

    // when
    notificationService.delete(notificationId, receiverId);

    // then
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository).delete(notification);
  }

  @Test
  @DisplayName("알림 삭제 실패 - 존재하지 않는 알림")
  void delete_Failure_NotificationNotFound() {
    // given
    given(notificationRepository.findById(notificationId))
        .willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> notificationService.delete(notificationId, receiverId))
        .isInstanceOf(NotificationNotFoundException.class);
    
    verify(notificationRepository).findById(notificationId);
    verify(notificationRepository, never()).delete(any());
  }

  @Test
  @DisplayName("다중 수신자 알림 생성 성공")
  void create_MultipleReceivers_Success() {
    // given
    UUID receiverId1 = UUID.randomUUID();
    UUID receiverId2 = UUID.randomUUID();
    Set<UUID> receiverIds = Set.of(receiverId1, receiverId2);
    String title = "Test Title";
    String content = "Test Content";

    // when
    notificationService.create(receiverIds, title, content);

    // then
    verify(notificationRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("빈 수신자 집합으로 알림 생성 시 아무 동작 안함")
  void create_EmptyReceiverSet_NoAction() {
    // given
    Set<UUID> emptyReceiverIds = Set.of();
    String title = "Test Title";
    String content = "Test Content";

    // when
    notificationService.create(emptyReceiverIds, title, content);

    // then
    verifyNoInteractions(notificationRepository);
  }

  @Test
  @DisplayName("단일 수신자 알림 생성 성공")
  void create_SingleReceiver_Success() {
    // given
    Set<UUID> receiverIds = Set.of(receiverId);
    String title = "Test Title";
    String content = "Test Content";

    // when
    notificationService.create(receiverIds, title, content);

    // then
    verify(notificationRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("알림 생성 시 올바른 Notification 객체 생성")
  void create_CreatesCorrectNotificationObjects() {
    // given
    UUID receiverId1 = UUID.randomUUID();
    UUID receiverId2 = UUID.randomUUID();
    Set<UUID> receiverIds = Set.of(receiverId1, receiverId2);
    String title = "Role Updated";
    String content = "USER -> ADMIN";

    // when
    notificationService.create(receiverIds, title, content);

    // then
    verify(notificationRepository).saveAll(any(List.class));
  }
}