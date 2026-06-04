package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class BinaryContentUploadFailureNotifierTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private NotificationRepository notificationRepository;

  @InjectMocks
  private BinaryContentUploadFailureNotifier notifier;

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  @DisplayName("notifyAdmins 성공: S3 업로드 최종 실패 정보를 ADMIN 알림으로 저장한다")
  void notifyAdmins_success() {
    UUID binaryContentId = UUID.randomUUID();
    User admin = new User("admin", "admin@test.com", "pw", UserRole.ADMIN, null);
    RuntimeException cause = new RuntimeException("S3 access denied");
    MDC.put("requestId", "request-123");

    given(userRepository.findAllByRole(UserRole.ADMIN)).willReturn(List.of(admin));

    notifier.notifyAdmins(binaryContentId, cause);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    then(notificationRepository).should().saveAll(captor.capture());
    Notification notification = captor.getValue().get(0);

    assertThat(notification.getReceiver()).isEqualTo(admin);
    assertThat(notification.getTitle()).isEqualTo("바이너리 데이터 저장에 실패했습니다.");
    assertThat(notification.getContent()).contains(
        "Task: S3 binary content upload",
        "RequestId: request-123",
        "BinaryContentId: " + binaryContentId,
        "Error: S3 access denied"
    );
  }
}
