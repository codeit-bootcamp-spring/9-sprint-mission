package com.sprint.mission.discodeit.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
  @DisplayName("토큰 로그인 성공: accessToken을 발급하고 Bearer 인증에 사용한다")
  void tokenLogin_success() throws Exception {
    createUser("jun", "jun@test.com", "password123");

    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .param("username", "jun")
            .param("password", "password123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.userDto.username").value("jun"))
        .andExpect(jsonPath("$.username").value("jun"))
        .andReturn();

    String authorization = loginResult.getResponse().getHeader("Authorization");
    assertNotNull(authorization);
    Cookie refreshToken = loginResult.getResponse().getCookie("refreshToken");
    assertNotNull(refreshToken);

    mockMvc.perform(post("/api/auth/logout")
            .header("Authorization", authorization))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users")
            .header("Authorization", authorization))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("jun"));

    mockMvc.perform(post("/api/auth/refresh")
            .cookie(refreshToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.userDto.username").value("jun"));
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
            .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated());
  }
}
