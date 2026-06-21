package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.security.jwt.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig
    implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor
      jwtAuthenticationChannelInterceptor;

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

  private AuthorizationChannelInterceptor
  authorizationChannelInterceptor() {

    return new AuthorizationChannelInterceptor(

        MessageMatcherDelegatingAuthorizationManager
            .builder()

            .anyMessage()
            .hasRole(Role.USER.name())

            .build()
    );
  }
}