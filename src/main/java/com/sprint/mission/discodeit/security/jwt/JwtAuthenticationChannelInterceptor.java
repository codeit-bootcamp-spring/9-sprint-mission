package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.DiscodeitUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor
    implements ChannelInterceptor {

  private final JwtTokenProvider jwtTokenProvider;
  private final DiscodeitUserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(
      Message<?> message,
      MessageChannel channel
  ) {

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor.class
        );

    if (accessor != null
        && StompCommand.CONNECT.equals(accessor.getCommand())) {

      String authorization =
          accessor.getFirstNativeHeader("Authorization");

      if (authorization == null
          || !authorization.startsWith("Bearer ")) {
        throw new IllegalArgumentException(
            "Authorization 헤더가 없습니다."
        );
      }

      String token =
          authorization.substring(7);

      if (!jwtTokenProvider.validateAccessToken(token)) {
        throw new IllegalArgumentException(
            "유효하지 않은 JWT 토큰입니다."
        );
      }

      String username =
          jwtTokenProvider.getUsernameFromToken(token);

      DiscodeitUserDetails userDetails =
          (DiscodeitUserDetails)
              userDetailsService.loadUserByUsername(username);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );

      accessor.setUser(authentication);
    }

    return message;
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {

    AuthorizationManager<Message<?>> authorizationManager =
        MessageMatcherDelegatingAuthorizationManager.builder()

            .anyMessage()
            .hasRole("USER")

            .build();

    return new AuthorizationChannelInterceptor(
        authorizationManager
    );
  }
}