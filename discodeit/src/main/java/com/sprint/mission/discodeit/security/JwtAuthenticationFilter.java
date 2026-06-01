package com.sprint.mission.discodeit.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.dto.data.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    try {
      JWTClaimsSet claims = jwtTokenProvider.validateAndGetClaims(token);

      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        filterChain.doFilter(request, response);
        return;
      }

      UserDto userDto = new UserDto(
          jwtTokenProvider.extractUserId(claims),
          jwtTokenProvider.extractUsername(claims),
          null,
          null,
          null,
          jwtTokenProvider.extractRole(claims)
      );
      DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, null);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception e) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }
}
