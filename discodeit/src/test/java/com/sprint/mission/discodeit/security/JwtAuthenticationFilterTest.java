package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  private static final String SECRET = "test-jwt-secret-key-for-mission-10-provider";

  @Mock
  private JwtRegistry jwtRegistry;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private FilterChain filterChain;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_WithBearerToken_SetsAuthentication() throws Exception {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(tokenProvider, jwtRegistry, userDetailsService);
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true,
        Role.USER);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    String token = tokenProvider.generateAccessToken(userDto);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    MockHttpServletResponse response = new MockHttpServletResponse();
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
    given(userDetailsService.loadUserByUsername(userDto.username())).willReturn(userDetails);

    filter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .isEqualTo(userDetails);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_WithoutBearerToken_DoesNotSetAuthentication() throws Exception {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(tokenProvider, jwtRegistry, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_WithInvalidToken_DoesNotSetAuthentication() throws Exception {
    JwtTokenProvider tokenProvider = new JwtTokenProvider(SECRET, 1800, 3600);
    JwtAuthenticationFilter filter =
        new JwtAuthenticationFilter(tokenProvider, jwtRegistry, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.any());
    verify(filterChain).doFilter(request, response);
  }
}
