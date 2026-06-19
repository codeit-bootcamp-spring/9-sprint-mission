package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.UserRole;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageAuthorizationContext;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
  private final RoleHierarchy roleHierarchy;

  public WebSocketConfig(
      JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor,
      RoleHierarchy roleHierarchy
  ) {
    this.jwtAuthenticationChannelInterceptor = jwtAuthenticationChannelInterceptor;
    this.roleHierarchy = roleHierarchy;
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/sub");
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor()
    );
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().access(userRoleAuthorizationManager())
            .build()
    );
  }

  private AuthorizationManager<MessageAuthorizationContext<?>> userRoleAuthorizationManager() {
    AuthorityAuthorizationManager<MessageAuthorizationContext<?>> authorizationManager =
        AuthorityAuthorizationManager.hasRole(UserRole.USER.name());
    authorizationManager.setRoleHierarchy(roleHierarchy);
    return authorizationManager;
  }
}
