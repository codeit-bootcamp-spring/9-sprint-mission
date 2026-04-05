package com.sprint.mission.discodeit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService;

  private UserDto savedUser;

  @BeforeEach
  void setUp() {
    UserCreateRequest request = new UserCreateRequest("홍길동", "test@test.com", "password123");
    savedUser = userService.create(request, Optional.empty());
  }

  @Test
  @DisplayName("전체 유저 목록 조회 성공")
  void findAll_success() throws Exception {
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].username").value("홍길동"));
  }

  @Test
  @DisplayName("유저 생성 성공")
  void create_success() throws Exception {
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("새유저", "new@test.com", "password123")
        )
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("새유저"))
        .andExpect(jsonPath("$.email").value("new@test.com"));
  }

  @Test
  @DisplayName("유저 생성 실패 - 이메일 중복")
  void create_fail_duplicateEmail() throws Exception {
    MockMultipartFile userCreateRequest = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserCreateRequest("다른유저", "test@test.com", "password123") // 중복 이메일
        )
    );

    mockMvc.perform(multipart("/api/users")
            .file(userCreateRequest))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_ALREADY_EXIST"));
  }

  @Test
  @DisplayName("유저 수정 성공")
  void update_success() throws Exception {
    MockMultipartFile userUpdateRequest = new MockMultipartFile(
        "userUpdateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(
            new UserUpdateRequest("수정된이름", "updated@test.com", "newpassword123")
        )
    );

    mockMvc.perform(multipart("/api/users/{userId}", savedUser.id())
            .file(userUpdateRequest)
            .with(req -> { req.setMethod("PATCH"); return req; }))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("수정된이름"));
  }

  @Test
  @DisplayName("유저 삭제 성공")
  void delete_success() throws Exception {
    mockMvc.perform(delete("/api/users/{userId}", savedUser.id()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("유저 삭제 실패 - 유저 없음")
  void delete_fail_notFound() throws Exception {
    mockMvc.perform(delete("/api/users/{userId}", java.util.UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }
}