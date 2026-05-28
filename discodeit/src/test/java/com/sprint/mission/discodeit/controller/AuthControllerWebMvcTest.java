package com.sprint.mission.discodeit.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.sprint.mission.discodeit.exception.user.InitialAdminRoleChangeNotAllowedException;
import com.sprint.mission.discodeit.security.JwtLoginSuccessHandler;
import com.sprint.mission.discodeit.security.JwtLogoutHandler;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenIssuer;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerWebMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private FilterChainProxy filterChainProxy;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private JwtLoginSuccessHandler jwtLoginSuccessHandler;

  @MockitoBean
  private JwtLogoutHandler jwtLogoutHandler;

  @MockitoBean
  private LoginFailureHandler loginFailureHandler;

  @MockitoBean
  private JwtTokenProvider jwtTokenProvider;

  @MockitoBean
  private JwtTokenIssuer jwtTokenIssuer;

  @MockitoBean
  private JwtRegistry jwtRegistry;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private JpaMetamodelMappingContext jpaMetamodelMappingContext;

  @Test
  @DisplayName("GET /api/auth/csrf-token 성공: 토큰 기반 인증에서 204를 반환한다")
  void getCsrfToken_success() throws Exception {
    mockMvc.perform(get("/api/auth/csrf-token"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("SecurityFilterChain에 RememberMeAuthenticationFilter가 등록되지 않는다")
  void securityFilterChain_hasNoRememberMeFilter() {
    boolean hasRememberMeFilter = filterChainProxy.getFilterChains().stream()
        .flatMap(chain -> chain.getFilters().stream())
        .anyMatch(filter -> filter.getClass().getName().contains("RememberMeAuthenticationFilter"));

    assertFalse(hasRememberMeFilter);
  }

  @Test
  @DisplayName("GET /actuator/health 실패: 인증되지 않으면 401 에러 JSON을 반환한다")
  void actuator_fail_unauthenticated() throws Exception {
    mockMvc.perform(get("/actuator/health"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("AUTH_401"))
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  @DisplayName("GET /actuator/health 실패: ADMIN 권한이 없으면 403 에러 JSON을 반환한다")
  void actuator_fail_forbiddenWithoutAdminRole() throws Exception {
    mockMvc.perform(get("/actuator/health")
            .with(user("jun").roles("USER")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("AUTH_403"))
        .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  @DisplayName("POST /api/auth/logout 성공: 로그아웃 후 204를 반환한다")
  void logout_success() throws Exception {
    mockMvc.perform(post("/api/auth/logout")
            .with(csrf()))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("POST /api/auth/logout 성공: CSRF 토큰이 없어도 204를 반환한다")
  void logout_success_withoutCsrf() throws Exception {
    mockMvc.perform(post("/api/auth/logout"))
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
            .with(user("admin").roles("ADMIN"))
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));
  }

  @Test
  @DisplayName("PUT /api/auth/role 성공: 정적 프론트의 newRole 필드명도 허용한다")
  void updateRole_success_withNewRoleAlias() throws Exception {
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
            .with(user("admin").roles("ADMIN"))
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(
                Map.of("userId", userId, "newRole", UserRole.CHANNEL_MANAGER)
            )))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value("CHANNEL_MANAGER"));
  }

  @Test
  @DisplayName("PUT /api/auth/role 실패: 초기 관리자 권한 변경은 409 에러 JSON을 반환한다")
  void updateRole_fail_initialAdminRoleChange() throws Exception {
    UUID userId = UUID.randomUUID();
    UserRoleUpdateRequest request = new UserRoleUpdateRequest(userId, UserRole.USER);

    given(userService.updateRole(request)).willThrow(
        new InitialAdminRoleChangeNotAllowedException(Map.of("userId", userId))
    );

    mockMvc.perform(put("/api/auth/role")
            .with(user("admin").roles("ADMIN"))
            .with(csrf())
            .contentType("application/json")
            .content(objectMapper.writeValueAsBytes(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("USER_409"))
        .andExpect(jsonPath("$.message").value("초기 관리자 계정의 권한은 변경할 수 없습니다."))
        .andExpect(jsonPath("$.status").value(409));
  }
}
