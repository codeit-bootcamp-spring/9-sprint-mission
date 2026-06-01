package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    //  Authorization 헤더에서 Bearer 토큰 추출
    String token = resolveToken(request);

    //  Bearer 토큰이 있을 때만 인증 시도
    if (token != null && jwtTokenProvider.validateToken(token)) {

      //  토큰에서 username 꺼내서 UserDetails 로드
      String username = jwtTokenProvider.getUsername(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      //  인증 객체 생성 후 SecurityContext에 등록
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("JWT 인증 완료: username={}", username);
    }

    //  다음 필터로 넘기기
    filterChain.doFilter(request, response);
  }

  // Authorization: Bearer <token> 에서 토큰 부분만 추출
  private String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}