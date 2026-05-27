package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // 🌟 인증 시 시스템 식별 기준(username 혹은 email)의 혼선을 방지하기 위해 정합성을 맞춰 저장합니다.
        User mockUser = new User(
            "testUser",
            "test@example.com",
            passwordEncoder.encode("password123!"),
            null
        );
        userRepository.save(mockUser);
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 성공")
    void login_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testUser", "password123!");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.username", is("testUser")))
            .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (존재하지 않는 사용자)")
    void login_Failure_UserNotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistentuser", "password123!");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (잘못된 비밀번호)")
    void login_Failure_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testUser", "WrongPassword!");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그인 API 통합 테스트 - 실패 (유효하지 않은 요청)")
    void login_Failure_InvalidRequest() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");
        String requestBody = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }
}
