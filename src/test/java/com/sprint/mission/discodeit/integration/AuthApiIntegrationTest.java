package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("로그인 API 통합 테스트 - 성공")
    void login_Success() throws Exception {
        // Given: 테스트 사용자 생성
        String username = "loginuser";
        String password = "Password1!";
        UserCreateRequest userRequest = new UserCreateRequest(username, "login@example.com", password);
        userService.create(userRequest, Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", password)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.username", is(username)));
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (존재하지 않는 사용자)")
    void login_Failure_UserNotFound() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "nonexistentuser")
                        .param("password", "Password1!")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (잘못된 비밀번호)")
    void login_Failure_InvalidCredentials() throws Exception {
        // Given: 사용자 생성
        String username = "loginuser2";
        userService.create(new UserCreateRequest(username, "login2@example.com", "Password1!"), Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("password", "WrongPassword1!")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}