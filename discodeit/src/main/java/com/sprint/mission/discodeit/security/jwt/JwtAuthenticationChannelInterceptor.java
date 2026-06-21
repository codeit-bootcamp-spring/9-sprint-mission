package com.sprint.mission.discodeit.security.jwt;

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
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor
    implements ChannelInterceptor {

  private final JwtTokenProvider tokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

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

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {

      String bearer =
          accessor.getFirstNativeHeader(
              "Authorization"
          );

      String token =
          bearer.substring(7);

      if (
          tokenProvider.validateAccessToken(token)
              &&
              jwtRegistry.hasActiveJwtInformationByAccessToken(token)
      ) {

        String username =
            tokenProvider.getUsernameFromToken(token);

        UserDetails userDetails =
            userDetailsService
                .loadUserByUsername(username);

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

        accessor.setUser(auth);
      }
    }

    return message;
  }
}