package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.exception.auth.InvalidPasswordException;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login - 성공: 로그인한다")
    void login_success() throws Exception {
        // given
        UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@email.com", null, true);
        given(authService.login(any(LoginRequest.class))).willReturn(userDto);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("testuser", "password123"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 실패: 사용자 없음 (보안상 401 통일)")
    void login_fail_userNotFound() throws Exception {
        // given
        willThrow(new InvalidPasswordException(Map.of("username", "nonexistent")))
            .given(authService).login(any(LoginRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("nonexistent", "password123"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 실패: 비밀번호 불일치")
    void login_fail_invalidPassword() throws Exception {
        // given
        willThrow(new InvalidPasswordException(Map.of("username", "testuser")))
            .given(authService).login(any(LoginRequest.class));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("testuser", "wrong"))))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"));
    }

    @Test
    @DisplayName("POST /api/auth/login - 실패: 유효성 검사 실패 (빈 사용자명)")
    void login_fail_validation() throws Exception {
        // given - 빈 사용자명 요청

        // when & then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("", "password123"))))
            .andExpect(status().isBadRequest());
    }
}
