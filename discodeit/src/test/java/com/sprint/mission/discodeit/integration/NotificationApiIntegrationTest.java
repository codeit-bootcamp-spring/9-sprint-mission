package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("알림 API 통합 테스트")
class NotificationApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("알림 목록 조회 API 통합 테스트")
  void findAllByReceiverId_IntegrationTest() throws Exception {
    // Given - Create a real test user
    UserDto testUser = userService.create(
        new UserCreateRequest("testuser", "test@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(testUser, "password");

    // Create test notifications for this user
    notificationService.create(Set.of(testUser.id()), "New Message", "You have a new message");
    notificationService.create(Set.of(testUser.id()), "Role Updated", "USER -> ADMIN");

    // When & Then
    mockMvc.perform(get("/api/notifications")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  @DisplayName("빈 알림 목록 조회 API 통합 테스트")
  void findAllByReceiverId_EmptyList_IntegrationTest() throws Exception {
    // Given - Create a real test user with no notifications
    UserDto testUser = userService.create(
        new UserCreateRequest("emptyuser", "empty@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(testUser, "password");

    // When & Then - No notifications created for this user
    mockMvc.perform(get("/api/notifications")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  @DisplayName("알림 삭제 API 접근 테스트")
  void delete_ApiAccess_IntegrationTest() throws Exception {
    // Given - Create a real test user
    UserDto testUser = userService.create(
        new UserCreateRequest("deleteuser", "delete@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(testUser, "password");
    UUID randomNotificationId = UUID.randomUUID();

    // When & Then - Test that the endpoint is accessible (will return 404 for random ID)
    mockMvc.perform(delete("/api/notifications/{notificationId}", randomNotificationId)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("존재하지 않는 알림 삭제 API 통합 테스트")
  void delete_NotFound_IntegrationTest() throws Exception {
    // Given - Create a real test user
    UserDto testUser = userService.create(
        new UserCreateRequest("notfounduser", "notfound@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(testUser, "password");
    UUID nonExistentNotificationId = UUID.randomUUID();

    // When & Then
    mockMvc.perform(delete("/api/notifications/{notificationId}", nonExistentNotificationId)
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("인증되지 않은 사용자의 알림 조회 요청")
  void findAllByReceiverId_Unauthorized() throws Exception {
    // When & Then - Spring Security returns 403 Forbidden for unauthorized requests
    mockMvc.perform(get("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("인증되지 않은 사용자의 알림 삭제 요청")
  void delete_Unauthorized() throws Exception {
    // Given
    UUID notificationId = UUID.randomUUID();

    // When & Then - Spring Security returns 403 Forbidden for unauthorized requests
    mockMvc.perform(delete("/api/notifications/{notificationId}", notificationId)
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("다중 수신자 알림 생성 통합 테스트")
  void multipleReceivers_NotificationCreation() throws Exception {
    // Given - Create test users
    UserDto testUser1 = userService.create(
        new UserCreateRequest("multi1", "multi1@example.com", "Password123!"),
        Optional.empty()
    );

    UserDto testUser2 = userService.create(
        new UserCreateRequest("multi2", "multi2@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails1 = new DiscodeitUserDetails(testUser1, "password");

    // When - Create notification for multiple users
    notificationService.create(Set.of(testUser1.id(), testUser2.id()), "Broadcast Message",
        "This message goes to multiple users");

    // Then - Test that first user can see their notification
    mockMvc.perform(get("/api/notifications")
            .with(user(userDetails1))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  @DisplayName("API 기본 접근 테스트")
  void basicApiAccess() throws Exception {
    // Given - Create a test user for authentication
    UserDto testUser = userService.create(
        new UserCreateRequest("apiuser", "api@example.com", "Password123!"),
        Optional.empty()
    );

    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(testUser, "password");

    // When & Then - Simply test that the API is accessible
    mockMvc.perform(get("/api/notifications")
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}