package com.sprint.mission.discodeit.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.SecurityConfig;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private LoginSuccessHandler loginSuccessHandler;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("GET /api/auth/csrf-token 성공: CSRF 토큰을 쿠키로 발급한다")
  void getCsrfToken_success() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNonAuthoritativeInformation())
        .andExpect(result -> {
          Cookie csrfTokenCookie = result.getResponse().getCookie("XSRF-TOKEN");

          assertNotNull(csrfTokenCookie);
          assertFalse(csrfTokenCookie.isHttpOnly());
        });
  }

  @Test
  @DisplayName("GET /api/auth/me 성공: 인증된 사용자 정보를 반환한다")
  void me_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserResponse userResponse = new UserResponse(userId, "jun", "jun@test.com", null, false);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userResponse, "encodedPassword");

    mockMvc.perform(get("/api/auth/me")
            .with(user(userDetails)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("jun"))
        .andExpect(jsonPath("$.email").value("jun@test.com"))
        .andExpect(jsonPath("$.online").value(false));
  }

  @Test
  @DisplayName("POST /api/auth/logout 성공: 로그아웃 후 204를 반환한다")
  void logout_success() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
            .with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("PUT /api/auth/role 성공: 사용자 권한을 수정한다")
  void updateRole_success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.CHANNEL_MANAGER);
    UserResponse response = new UserResponse(
        userId,
        "jun",
        "jun@test.com",
        null,
        false,
        UserRole.CHANNEL_MANAGER
    );

    given(userService.updateRole(request)).willReturn(response);

    mockMvc.perform(put("/api/auth/role")
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));
  }
}
