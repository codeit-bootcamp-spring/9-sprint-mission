package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserIntegrationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  @Test
  @DisplayName("통합 테스트: 사용자 생성 - 성공")
  void createUser_Integration_Success() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("integrationUser", "integ@email.com", "password123");
    MockMultipartFile jsonPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    mvc.perform(multipart("/api/users")
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("integrationUser"))
        .andExpect(jsonPath("$.email").value("integ@email.com"));
  }

  @Test
  @DisplayName("통합 테스트: 사용자 목록 조회 - 성공")
  void findAllUsers_Integration_Success() throws Exception {
    // given
    userService.create(new UserCreateRequest("user1", "user1@email.com", "pass"), Optional.empty());
    userService.create(new UserCreateRequest("user2", "user2@email.com", "pass"), Optional.empty());

    // when & then
    mvc.perform(get("/api/users")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
  }

  @Test
  @DisplayName("통합 테스트: 사용자 정보 수정 - 성공")
  void updateUser_Integration_Success() throws Exception {
    // given
    UserDto savedUser = userService.create(new UserCreateRequest("oldName", "old@email.com", "pass"), Optional.empty());
    UUID targetUserId = savedUser.id();

    UserUpdateRequest updateRequest = new UserUpdateRequest("newName", "new@email.com", "newPass123");
    MockMultipartFile jsonPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsString(updateRequest).getBytes(StandardCharsets.UTF_8)
    );

    // when & then
    // Multipart 요청을 PATCH 메서드로 변경하여 전송
    mvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", targetUserId)
            .file(jsonPart)
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newName"))
        .andExpect(jsonPath("$.email").value("new@email.com"));
  }

  @Test
  @DisplayName("통합 테스트: 사용자 삭제 - 성공")
  void deleteUser_Integration_Success() throws Exception {
    // given
    UserDto savedUser = userService.create(new UserCreateRequest("deleteTarget", "delete@email.com", "pass"), Optional.empty());
    UUID targetUserId = savedUser.id();

    // when & then
    mvc.perform(delete("/api/users/{userId}", targetUserId))
        .andExpect(status().isNoContent());

    // 삭제 후 다시 조회 시 존재하지 않음을 확인 (프로젝트의 404 예외 응답 검증)
    mvc.perform(delete("/api/users/{userId}", targetUserId))
        .andExpect(status().isNotFound());
  }
}