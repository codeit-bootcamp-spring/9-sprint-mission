package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
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

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/sub");
    config.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  // 웹소켓 인증/인가 처리
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor(),
        authorizationChannelInterceptor());
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT,
                SimpMessageType.UNSUBSCRIBE).permitAll()
            .simpTypeMatchers(SimpMessageType.SUBSCRIBE, SimpMessageType.MESSAGE).authenticated()
            .anyMessage().hasRole(Role.USER.name())
            .build()
    );
  }

}
