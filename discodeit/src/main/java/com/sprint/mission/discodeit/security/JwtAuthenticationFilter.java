package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  private final JwtRegistry jwtRegistry;

  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {

    String token = resolveToken(request);

    if (token != null) {
      boolean isValid = jwtTokenProvider.validateToken(token);
      boolean isInRegistry = jwtRegistry.hasActiveJwtInformationByAccessToken(token);

      log.info("들어온 토큰: {}", token.substring(0, 15) + "...");
      log.info("1. 토큰 유효성 검사 통과 여부: {}", isValid);
      log.info("2. 레지스트리(대장) 존재 여부: {}", isInRegistry);
    }

    if (token != null && jwtTokenProvider.validateToken(token)
        && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

      String username = jwtTokenProvider.getUsername(token);

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.info("JWT Authentication Success: {}", username);
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(
      HttpServletRequest request
  ) {

    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

      return bearerToken.substring(7);
    }

    return null;
  }
}