package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      accessor.setUser(authenticate(accessor));
      return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
    }

    return message;
  }

  private UsernamePasswordAuthenticationToken authenticate(StompHeaderAccessor accessor) {
    String token = resolveBearerToken(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION));
    if (!jwtRegistry.hasActiveJwtInformationByAccessToken(token)) {
      throw new BadCredentialsException("Inactive token");
    }

    String username = jwtTokenProvider.getUsername(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
  }

  private String resolveBearerToken(String authorization) {
    if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
      throw new BadCredentialsException("Bearer token is required");
    }
    return authorization.substring(BEARER_PREFIX.length());
  }
}
