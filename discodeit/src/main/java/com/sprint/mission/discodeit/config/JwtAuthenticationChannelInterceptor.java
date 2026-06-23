package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.basic.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      // 검증 로직
      String token = resolveToken(accessor);

      if (!jwtTokenProvider.validateToken(token)) {
        throw new BadCredentialsException("유효하지 않은 토큰입니다.");
      }
      if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
        throw new BadCredentialsException("무효화된 토큰입니다.");
      }

      String username = jwtTokenProvider.getUsername(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      accessor.setUser(authentication);
    }
    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String header = accessor.getFirstNativeHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      throw new BadCredentialsException("Authorization 헤더가 없습니다.");
    }
    return header.substring(7);
  }
}
