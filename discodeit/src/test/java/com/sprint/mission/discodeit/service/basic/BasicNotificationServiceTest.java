package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private NotificationMapper notificationMapper;

  @Test
  void delete_DeletesOwnNotification() {
    BasicNotificationService service =
        new BasicNotificationService(notificationRepository, notificationMapper);
    UUID notificationId = UUID.randomUUID();
    UUID requesterId = UUID.randomUUID();
    given(notificationRepository.findReceiverIdById(notificationId))
        .willReturn(Optional.of(requesterId));

    service.delete(notificationId, requesterId);

    verify(notificationRepository).deleteById(notificationId);
  }

  @Test
  void delete_FailsWhenRequesterIsNotReceiver() {
    BasicNotificationService service =
        new BasicNotificationService(notificationRepository, notificationMapper);
    UUID notificationId = UUID.randomUUID();
    given(notificationRepository.findReceiverIdById(notificationId))
        .willReturn(Optional.of(UUID.randomUUID()));

    assertThatThrownBy(() -> service.delete(notificationId, UUID.randomUUID()))
        .isInstanceOf(AccessDeniedException.class);
  }

  @Test
  void delete_FailsWhenNotificationDoesNotExist() {
    BasicNotificationService service =
        new BasicNotificationService(notificationRepository, notificationMapper);
    UUID notificationId = UUID.randomUUID();
    given(notificationRepository.findReceiverIdById(notificationId)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.delete(notificationId, UUID.randomUUID()))
        .isInstanceOf(NotificationNotFoundException.class);
  }
}
