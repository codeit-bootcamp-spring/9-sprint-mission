package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private NotificationMapper notificationMapper;

  @InjectMocks
  private BasicNotificationService notificationService;

  @Test
  @DisplayName("findAllByReceiverId 성공: 수신자 알림 목록을 DTO로 반환한다")
  void findAllByReceiverId_success() {
    UUID receiverId = UUID.randomUUID();
    Notification notification = new Notification(
        new User("jun", "jun@test.com", "pw", null),
        "title",
        "content"
    );
    NotificationDto dto = new NotificationDto(
        UUID.randomUUID(),
        Instant.now(),
        receiverId,
        "title",
        "content"
    );

    given(notificationRepository.findAllByReceiverIdWithReceiver(receiverId))
        .willReturn(List.of(notification));
    given(notificationMapper.toDto(notification)).willReturn(dto);

    List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

    assertEquals(List.of(dto), result);
  }

  @Test
  @DisplayName("delete 성공: 본인의 알림이면 삭제한다")
  void delete_success() {
    UUID notificationId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    User receiver = new User("jun", "jun@test.com", "pw", null);
    ReflectionTestUtils.setField(receiver, "id", requesterId);
    Notification notification = new Notification(receiver, "title", "content");

    given(notificationRepository.findByIdWithReceiver(notificationId))
        .willReturn(Optional.of(notification));

    notificationService.delete(notificationId, requesterId);

    then(notificationRepository).should().delete(notification);
  }

  @Test
  @DisplayName("delete 실패: 다른 사용자의 알림이면 ACCESS_DENIED가 발생한다")
  void delete_fail_accessDenied() {
    UUID notificationId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    User receiver = new User("jun", "jun@test.com", "pw", null);
    ReflectionTestUtils.setField(receiver, "id", UUID.randomUUID());
    Notification notification = new Notification(receiver, "title", "content");

    given(notificationRepository.findByIdWithReceiver(notificationId))
        .willReturn(Optional.of(notification));

    DiscodeitException ex = assertThrows(DiscodeitException.class,
        () -> notificationService.delete(notificationId, requesterId));

    assertEquals(ErrorCode.ACCESS_DENIED, ex.getErrorCode());
  }

  @Test
  @DisplayName("delete 실패: 알림이 없으면 NOTIFICATION_NOT_FOUND가 발생한다")
  void delete_fail_notFound() {
    UUID notificationId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();

    given(notificationRepository.findByIdWithReceiver(notificationId)).willReturn(Optional.empty());

    DiscodeitException ex = assertThrows(DiscodeitException.class,
        () -> notificationService.delete(notificationId, requesterId));

    assertEquals(ErrorCode.NOTIFICATION_NOT_FOUND, ex.getErrorCode());
  }
}
