package com.sprint.mission.discodeit.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import com.sprint.mission.discodeit.security.jwt.store.JwtRegistry;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      String token = extractToken(request);

      if (token != null && jwtTokenProvider.validateToken(token)) {
        if (jwtTokenProvider.isRefreshToken(token)) {
          // refresh token은 인증에 사용하지 않음
          filterChain.doFilter(request, response);
          return;
        }

        // ensure token is active in registry
        if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
          log.debug("Access token not active in registry");
          filterChain.doFilter(request, response);
          return;
        }

        String username = jwtTokenProvider.getUsername(token);
        if (username != null) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.debug("JWT 인증 성공 - username: {}", username);
        }
      }
    } catch (Exception e) {
      log.debug("JWT 인증 처리 실패: {}", e.getMessage());
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}


