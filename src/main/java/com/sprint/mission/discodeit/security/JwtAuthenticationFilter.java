package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // 1. Authorization 헤더에서 Bearer 토큰 추출
    String token = resolveToken(request);

    // 2. 토큰이 없으면 다음 필터로 넘어감 (인증 시도 안 함)
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    // 3. 토큰 유효성 검사
    if (jwtTokenProvider.isValidToken(token)) {

      // 4. 토큰에서 username(subject) 추출 후 UserDetails 로드
      String username = jwtTokenProvider.getSubject(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // 5. 인증 객체 생성 및 SecurityContext에 등록
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,       // principal
              null,              // credentials (토큰 인증이므로 비밀번호 불필요)
              userDetails.getAuthorities()
          );
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 6. 다음 필터로 넘어감 (토큰이 유효하지 않아도 예외를 던지지 않음)
    //    → 인증이 필요한 엔드포인트라면 SecurityConfig의 authorizeHttpRequests에서 막힘
    filterChain.doFilter(request, response);
  }

  /**
   * Authorization 헤더에서 Bearer 토큰만 꺼내 반환
   * - 헤더가 없거나 Bearer로 시작하지 않으면 null 반환
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

    if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length()); // "Bearer " 제거 후 토큰만 반환
    }

    return null;
  }
}