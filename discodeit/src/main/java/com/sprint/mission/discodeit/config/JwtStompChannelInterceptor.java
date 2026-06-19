package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.util.Objects;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtStompChannelInterceptor implements ChannelInterceptor {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String PUBLISH_PREFIX = "/pub";
  private static final String SUBSCRIBE_PREFIX = "/sub";

  private final JwtTokenProvider jwtTokenProvider;
  private final JwtRegistry jwtRegistry;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    StompCommand command = accessor.getCommand();

    if (StompCommand.CONNECT.equals(command)) {
      accessor.setUser(authenticate(accessor));
    }

    if (requiresAuthenticatedUser(command, accessor.getDestination())
        && accessor.getUser() == null) {
      throw new BadCredentialsException("WebSocket authentication is required");
    }

    return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
  }

  private Authentication authenticate(StompHeaderAccessor accessor) {
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

  private boolean requiresAuthenticatedUser(StompCommand command, String destination) {
    if (!(StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command))) {
      return false;
    }
    if (destination == null) {
      return false;
    }
    return Objects.equals(destination, PUBLISH_PREFIX)
        || destination.startsWith(PUBLISH_PREFIX + "/")
        || Objects.equals(destination, SUBSCRIBE_PREFIX)
        || destination.startsWith(SUBSCRIBE_PREFIX + "/");
  }
}
