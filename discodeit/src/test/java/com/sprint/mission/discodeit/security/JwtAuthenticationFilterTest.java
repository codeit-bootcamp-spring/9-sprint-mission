package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import jakarta.servlet.FilterChain;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtAuthenticationFilterTest {

  private final JwtTokenProvider jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
  private final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
  private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
      jwtTokenProvider,
      userDetailsService
  );

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Authorization 헤더에 Bearer 토큰이 있으면 인증 객체를 저장한다")
  void doFilterInternal_withBearerToken_setsAuthentication() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = Mockito.mock(FilterChain.class);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, false),
        "encodedPassword"
    );

    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
    given(jwtTokenProvider.getUsername("access-token")).willReturn("jun");
    given(userDetailsService.loadUserByUsername("jun")).willReturn(userDetails);

    filter.doFilter(request, response, filterChain);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    assertThat(authentication.getAuthorities())
        .extracting("authority")
        .containsExactly("ROLE_USER");
    then(filterChain).should().doFilter(request, response);
  }

  @Test
  @DisplayName("Bearer 토큰이 없으면 인증을 시도하지 않는다")
  void doFilterInternal_withoutBearerToken_doesNotAuthenticate() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    FilterChain filterChain = Mockito.mock(FilterChain.class);

    filter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    then(jwtTokenProvider).should(never()).getUsername(Mockito.anyString());
    then(userDetailsService).shouldHaveNoInteractions();
    then(filterChain).should().doFilter(request, response);
  }
}
