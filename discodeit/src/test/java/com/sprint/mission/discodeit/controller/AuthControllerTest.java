package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtInformation;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private JwtRegistry jwtRegistry;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @MockitoBean
  private AuthService authService;

  @Test
  @DisplayName("리프레시 토큰으로 엑세스 토큰 재발급 - 성공")
  void refresh_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, false, Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "encodedPassword");
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
    String accessToken = jwtTokenProvider.createAccessToken(authentication);
    String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
    jwtRegistry.registerJwtInformation(new JwtInformation(
        userDto,
        accessToken,
        refreshToken,
        jwtTokenProvider.getExpirationTime(refreshToken)
    ));

    given(userDetailsService.loadUserByUsername("testuser")).willReturn(userDetails);

    mockMvc.perform(post("/api/auth/refresh")
            .with(csrf())
            .cookie(new Cookie("REFRESH_TOKEN", refreshToken)))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("REFRESH_TOKEN"))
        .andExpect(jsonPath("$.accessToken").isString())
        .andExpect(jsonPath("$.userDto.id").value(userId.toString()))
        .andExpect(jsonPath("$.userDto.username").value("testuser"));
  }

  @Test
  @DisplayName("리프레시 토큰으로 엑세스 토큰 재발급 - 유효하지 않은 토큰")
  void refresh_InvalidRefreshToken() throws Exception {
    mockMvc.perform(post("/api/auth/refresh")
            .with(csrf())
            .cookie(new Cookie("REFRESH_TOKEN", "invalid-token")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  @DisplayName("리프레시 토큰으로 엑세스 토큰 재발급 - 쿠키 없음")
  void refresh_MissingRefreshToken() throws Exception {
    mockMvc.perform(post("/api/auth/refresh")
            .with(csrf()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  @DisplayName("권한 업데이트 - 성공")
  void updateRole_Success() throws Exception {
    UUID userId = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);
    UserDto updatedUserDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.ADMIN
    );
    UserDto mockUserDto = new UserDto(userId, "testuser", "test@example.com", null, false,
        Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(mockUserDto, "password");

    given(authService.updateRole(any(RoleUpdateRequest.class))).willReturn(updatedUserDto);

    mockMvc.perform(put("/api/auth/role")
            .with(csrf())
            .with(user(userDetails))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  @Test
  @DisplayName("권한 업데이트 - 인증되지 않은 사용자")
  void updateRole_Unauthorized() throws Exception {
    UUID userId = UUID.randomUUID();
    RoleUpdateRequest request = new RoleUpdateRequest(userId, Role.ADMIN);

    mockMvc.perform(put("/api/auth/role")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}
