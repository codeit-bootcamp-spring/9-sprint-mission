package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
    implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor
      jwtAuthenticationChannelInterceptor;

  @Override
  public void configureMessageBroker(
      MessageBrokerRegistry config
  ) {
    config.enableSimpleBroker("/sub");
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(
      StompEndpointRegistry registry
  ) {
    registry.addEndpoint("/ws")
        .withSockJS();
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {

    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage()
            .hasRole("USER")
            .build()
    );
  }

  @Override
  public void configureClientInboundChannel(
      ChannelRegistration registration
  ) {

    registration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor()
    );
  }
}