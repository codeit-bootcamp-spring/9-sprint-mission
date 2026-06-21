package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.config.interceptor.JwtAuthenticationChannelInterceptor;
import com.sprint.mission.discodeit.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
  private final RoleHierarchy roleHierarchy;

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/pub");
    registry.enableSimpleBroker("/sub");
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    AuthorityAuthorizationManager<MessageAuthorizationContext<?>> authorityAuthorizationManager =
        AuthorityAuthorizationManager.hasRole(Role.USER.name());
    authorityAuthorizationManager.setRoleHierarchy(roleHierarchy);

    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().access(authorityAuthorizationManager)
            .build()
    );
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor()
    );
  }
}

