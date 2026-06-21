package com.sprint.mission.discodeit.security.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private DiscodeitUserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private PrintWriter printWriter;

  @Mock
  private JwtRegistry jwtRegistry;

  private JwtAuthenticationFilter jwtAuthenticationFilter;
  private DiscodeitUserDetails userDetails;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    jwtAuthenticationFilter = new JwtAuthenticationFilter(tokenProvider, userDetailsService,
        objectMapper, jwtRegistry);

    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(
        userId,
        "testuser",
        "test@example.com",
        null,
        false,
        Role.USER
    );

    userDetails = new DiscodeitUserDetails(userDto, "encoded-password");

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  @DisplayName("JWT 인증 필터 - 유효한 토큰으로 인증 성공")
  void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
    // Given
    String token = "valid.jwt.token";
    String username = "testuser";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    given(tokenProvider.validateAccessToken(token)).willReturn(true);
    given(tokenProvider.getUsernameFromToken(token)).willReturn(username);
    given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(securityContext).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("JWT 인증 필터 - 토큰 없음, 인증 설정하지 않음")
  void doFilterInternal_NoToken_DoesNotSetAuthentication() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn(null);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(securityContext, never()).setAuthentication(any());
    verify(tokenProvider, never()).validateAccessToken(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  @DisplayName("JWT 인증 필터 - 잘못된 토큰, 인증 설정하지 않음")
  void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws Exception {
    // Given
    String token = "invalid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    given(tokenProvider.validateAccessToken(token)).willReturn(false);
    when(response.getWriter()).thenReturn(printWriter);

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(securityContext, never()).setAuthentication(any());
    verify(userDetailsService, never()).loadUserByUsername(any());
    verify(filterChain, never()).doFilter(request, response);
    verify(response).setStatus(401);
  }

  @Test
  @DisplayName("JWT 인증 필터 - Bearer 없는 Authorization 헤더, 인증 설정하지 않음")
  void doFilterInternal_NonBearerToken_DoesNotSetAuthentication() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn("Basic dGVzdDp0ZXN0");

    // When
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(securityContext, never()).setAuthentication(any());
    verify(tokenProvider, never()).validateAccessToken(any());
    verify(filterChain).doFilter(request, response);
  }
}