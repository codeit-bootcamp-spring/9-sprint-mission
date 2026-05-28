package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import jakarta.servlet.http.Cookie;
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
class AuthApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Transactional
  @DisplayName("remember-me 로그인 성공: 세션 없이 RememberMe 쿠키만으로 현재 사용자를 조회한다")
  void rememberMeLogin_success() throws Exception {
    createUser("jun", "jun@test.com", "password123");

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .with(csrf())
            .param("username", "jun")
            .param("password", "password123")
            .param("remember-me", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("jun"))
        .andReturn();

    Cookie rememberMeCookie = loginResult.getResponse().getCookie("remember-me");
    assertNotNull(rememberMeCookie);

    mockMvc.perform(get("/api/auth/me")
            .cookie(rememberMeCookie))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("jun"))
        .andExpect(jsonPath("$.email").value("jun@test.com"));
  }

  private void createUser(String username, String email, String password) throws Exception {
    MockMultipartFile createPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(new UserCreateRequest(username, email, password))
    );

    mockMvc.perform(multipart("/api/users")
            .file(createPart)
            .with(csrf())
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }
}
