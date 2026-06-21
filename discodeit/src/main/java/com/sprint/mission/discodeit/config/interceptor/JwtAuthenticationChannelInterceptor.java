package com.sprint.mission.discodeit.config.interceptor;

import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import com.sprint.mission.discodeit.service.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService discodeitUserDetailsService;
  private final JwtRegistry jwtRegistry;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    // CONNECT 프레임일 때만 엑세스 토큰을 검증합니다.
    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor);

      if (StringUtils.hasText(token)
          && jwtTokenProvider.validateToken(token)
          && jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {

        String username = jwtTokenProvider.getUsername(token);
        UserDetails userDetails = discodeitUserDetailsService.loadUserByUsername(username);

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // [요구사항] 인증이 완료되면 SecurityContext가 아닌 accessor 객체에 저장
        accessor.setUser(authentication);
      }
    }
    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String bearerToken = accessor.getFirstNativeHeader("Authorization");
    if (StringUtils.hasText(bearerToken)) {
      bearerToken = bearerToken.trim();
      if (bearerToken.startsWith("Bearer ")) {
        return bearerToken.substring(7).trim();
      }
    }
    return null;
  }
}