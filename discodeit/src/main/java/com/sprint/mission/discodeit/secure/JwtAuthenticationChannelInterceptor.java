package com.sprint.mission.discodeit.secure;

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
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
        message, StompHeaderAccessor.class
    );

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authHeader = accessor.getFirstNativeHeader("Authorization");

      if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);

        if (jwtTokenProvider.validateToken(token)) {
          String username = jwtTokenProvider.getUsername(token);
          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(
                  userDetails,
                  null,
                  userDetails.getAuthorities()
              );
          accessor.setUser(authentication);
          log.debug("웹소켓 인증 성공: username={}", username);
        } else {
          log.warn("웹소켓 인증 실패: 유효하지 않은 토큰");
        }
      }
    }

    return message;
  }
}