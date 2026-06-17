package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.interceptor.JwtAuthenticationChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;

  @Override
  public void registerStompEndpoints(
      org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/sub");
    registry.setApplicationDestinationPrefixes("/pub");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
    channelRegistration.interceptors(
        jwtAuthenticationChannelInterceptor,
        new SecurityContextChannelInterceptor()
        , authorizationChannelInterceptor()
    );
  }

  private AuthorizationChannelInterceptor authorizationChannelInterceptor() {
    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .anyMessage().hasRole("USER").build()

    );
  }

}
