package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

    private MockMultipartFile createUserRequestPart(String username, String email)
        throws Exception {
        UserCreateRequest request = new UserCreateRequest(username, email, "password1234");
        return new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));
    }

    @Test
    @DisplayName("사용자 생성 → 목록 조회 → 삭제 통합 테스트")
    void createAndFindAndDelete() throws Exception {
        // 생성
        MvcResult createResult = mockMvc.perform(
                multipart("/api/users").file(createUserRequestPart("testuser", "test@email.com")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andReturn();

        String userId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 목록 조회
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.username == 'testuser')]").exists());

        // 삭제
        mockMvc.perform(delete("/api/users/{userId}", userId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("중복 이메일로 사용자 생성 시 409 응답")
    void create_fail_duplicateEmail() throws Exception {
        // 첫 번째 사용자 생성
        mockMvc.perform(
                multipart("/api/users").file(createUserRequestPart("user1", "dup@email.com")))
            .andExpect(status().isCreated());

        // 같은 이메일로 두 번째 사용자 생성 시도
        mockMvc.perform(
                multipart("/api/users").file(createUserRequestPart("user2", "dup@email.com")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USER"));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시 404 응답")
    void delete_fail_notFound() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("유효성 검사 실패 - 짧은 비밀번호")
    void create_fail_validation() throws Exception {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@email.com", "short");
        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/users").file(requestPart))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("프로필 이미지와 함께 사용자를 생성한다")
    void create_withProfile() throws Exception {
        MockMultipartFile requestPart = createUserRequestPart("profileuser", "profile@email.com");
        MockMultipartFile profileFile = new MockMultipartFile(
            "profile", "avatar.png", "image/png", "fake-image-data".getBytes());

        mockMvc.perform(multipart("/api/users").file(requestPart).file(profileFile))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("profileuser"));
    }

    @Test
    @DisplayName("로그인 → 사용자 상태 업데이트 통합 테스트")
    void loginAndUpdateStatus() throws Exception {
        // 사용자 생성
        MvcResult createResult = mockMvc.perform(
                multipart("/api/users").file(createUserRequestPart("loginuser", "login@email.com")))
            .andExpect(status().isCreated())
            .andReturn();
        String userId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 로그인
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new com.sprint.mission.discodeit.dto.request.LoginRequest("loginuser", "password1234"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("loginuser"));

        // 상태 업데이트
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest(
                        java.time.Instant.now()))))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 정보 수정 통합 테스트")
    void updateUser() throws Exception {
        // 생성
        MvcResult createResult = mockMvc.perform(
                multipart("/api/users").file(createUserRequestPart("origuser", "orig@email.com")))
            .andExpect(status().isCreated())
            .andReturn();
        String userId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        // 수정
        com.sprint.mission.discodeit.dto.request.UserUpdateRequest updateReq =
            new com.sprint.mission.discodeit.dto.request.UserUpdateRequest("newname", null, null);
        MockMultipartFile updatePart = new MockMultipartFile(
            "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(updateReq));

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(updatePart)
                .with(req -> { req.setMethod("PATCH"); return req; }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("newname"));
    }
}
