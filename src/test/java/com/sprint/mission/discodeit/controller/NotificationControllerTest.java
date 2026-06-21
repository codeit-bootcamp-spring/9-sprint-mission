package com.sprint.mission.discodeit.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.ResponseEntity;

@WebMvcTest(value = NotificationController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = ".*\\.security\\.jwt\\..*"))
class NotificationControllerTest {


  @MockitoBean
  private NotificationService notificationService;

  @Autowired
  private NotificationController notificationController;

  @Test
  @DisplayName("알림 목록 조회 성공 테스트")
  void findAllByReceiverId_Success() {
    // Given
    UUID receiverId = UUID.randomUUID();
    UUID notificationId1 = UUID.randomUUID();
    UUID notificationId2 = UUID.randomUUID();
    Instant now = Instant.now();

    UserDto userDto = new UserDto(receiverId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");

    List<NotificationDto> notifications = List.of(
        new NotificationDto(
            notificationId1,
            now.minusSeconds(60),
            receiverId,
            "새 메시지",
            "user1이 메시지를 보냈습니다."
        ),
        new NotificationDto(
            notificationId2,
            now.minusSeconds(120),
            receiverId,
            "권한 변경",
            "USER -> ADMIN"
        )
    );

    given(notificationService.findAllByReceiverId(receiverId)).willReturn(notifications);

    // When
    ResponseEntity<List<NotificationDto>> response = notificationController.findAllByReceiverId(principal);

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    List<NotificationDto> result = response.getBody();
    assertThat(result).isNotNull()
        .hasSize(2);
    assertThat(result.get(0).id()).isEqualTo(notificationId1);
    assertThat(result.get(0).title()).isEqualTo("새 메시지");
    assertThat(result.get(1).id()).isEqualTo(notificationId2);
    assertThat(result.get(1).title()).isEqualTo("권한 변경");
  }

  @Test
  @DisplayName("알림 목록 조회 - 빈 목록")
  void findAllByReceiverId_EmptyList() {
    // Given
    UUID receiverId = UUID.randomUUID();
    UserDto userDto = new UserDto(receiverId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");

    given(notificationService.findAllByReceiverId(receiverId)).willReturn(List.of());

    // When
    ResponseEntity<List<NotificationDto>> response = notificationController.findAllByReceiverId(principal);

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    List<NotificationDto> result = response.getBody();
    assertThat(result).isNotNull()
        .isEmpty();
  }

  @Test
  @DisplayName("알림 삭제 성공 테스트")
  void delete_Success() {
    // Given
    UUID receiverId = UUID.randomUUID();
    UUID notificationId = UUID.randomUUID();
    UserDto userDto = new UserDto(receiverId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");

    willDoNothing().given(notificationService).delete(notificationId, receiverId);

    // When
    ResponseEntity<Void> response = notificationController.delete(principal, notificationId);

    // Then
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getStatusCode().value()).isEqualTo(204);
  }

  @Test
  @DisplayName("알림 삭제 실패 테스트 - 존재하지 않는 알림")
  void delete_Failure_NotificationNotFound() {
    // Given
    UUID receiverId = UUID.randomUUID();
    UUID nonExistentNotificationId = UUID.randomUUID();
    UserDto userDto = new UserDto(receiverId, "testuser", "test@example.com", null, true, Role.USER);
    DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "password");

    willThrow(NotificationNotFoundException.withId(nonExistentNotificationId))
        .given(notificationService).delete(nonExistentNotificationId, receiverId);

    // When & Then
    assertThatThrownBy(() -> notificationController.delete(principal, nonExistentNotificationId))
        .isInstanceOf(NotificationNotFoundException.class);
  }
}