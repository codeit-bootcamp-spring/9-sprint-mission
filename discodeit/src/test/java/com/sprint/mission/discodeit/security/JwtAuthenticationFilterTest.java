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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock
  private JwtTokenProvider jwtTokenProvider;
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
  void doFilterInternalAuthenticatesWhenBearerTokenIsValid() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    String token = "valid-access-token";
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, false, Role.USER),
        "password"
    );

    request.addHeader("Authorization", "Bearer " + token);
    given(jwtTokenProvider.validateAccessToken(token)).willReturn(true);
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
    given(jwtTokenProvider.getUserIdFromToken(token)).willReturn("testuser");
    given(userDetailsService.loadUserByUsername("testuser")).willReturn(userDetails);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userDetails);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternalSkipsAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(jwtTokenProvider, never()).validateAccessToken(org.mockito.ArgumentMatchers.anyString());
    verify(jwtRegistry, never()).hasActiveJwtInformationByAccessToken(org.mockito.ArgumentMatchers.anyString());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternalSkipsAuthenticationWhenTokenIsInvalid() throws Exception {
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, jwtRegistry, userDetailsService);
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    String token = "invalid-access-token";

    request.addHeader("Authorization", "Bearer " + token);
    given(jwtTokenProvider.validateAccessToken(token)).willReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    verify(filterChain).doFilter(request, response);
  }
}
