package com.sprint.mission.discodeit.config;

import com.nimbusds.jwt.JWTClaimsSet;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        String token = authorizationHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
          try {
            JWTClaimsSet claimsSet = jwtTokenProvider.getClaims(token);
            String username = claimsSet.getSubject();

            if (username != null) {
              UserDetails userDetails = userDetailsService.loadUserByUsername(username);
              UsernamePasswordAuthenticationToken authentication =
                  new UsernamePasswordAuthenticationToken(userDetails, null,
                      userDetails.getAuthorities());

              accessor.setUser(authentication);
              log.info("[WebSocket] 인증 성공: 사용자 = {}", username);
            }
          } catch (ParseException e) {
            log.error("[WebSocket] JWT 토큰 파싱 중 오류가 발생했습니다.", e);
          }
        } else {
          log.warn("[WebSocket] 유효하지 않은 JWT 토큰입니다.");
        }
      }

    }

    return message;
  }
}