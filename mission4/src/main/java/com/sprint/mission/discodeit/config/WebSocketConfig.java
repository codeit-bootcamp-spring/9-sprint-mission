package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.interceptor.JwtAuthenticationChannelInterceptor;
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
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final JwtAuthenticationChannelInterceptor jwtAuthenticationChannelInterceptor;
  private final RoleHierarchy roleHierarchy;

  @Override
  public void registerStompEndpoints(
      org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").
        setAllowedOrigins("http://localhost:3000")
        .withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 주의: 커스텀 구분자("."))를 쓰면 안 됩니다.
    // applicationDestinationPrefixes("/pub")와 @MessageMapping("/messages")를
    // 결합(PathMatcher.combine)할 때도 이 구분자가 쓰이는데, "."로 바꾸면
    // "/pub" + "/messages" 가 "/pub/messages"가 아니라 "/pub./messages"로 합쳐져서
    // 실제 프론트 요청 경로("/pub/messages")와 매핑이 어긋나 핸들러가 아예 호출되지 않았습니다.
    // "/sub/channels.{id}.messages" 같은 토픽 이름은 와일드카드 없는 완전 일치 문자열이라
    // 기본 구분자("/")를 써도 정상적으로 매칭됩니다.
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
    // hasRole(...)만 쓰면 RoleHierarchy(ADMIN > CHANNEL_MANAGER > USER)가 적용되지 않아서
    // ADMIN 계정이 정작 USER 권한이 필요한 STOMP 메시지를 못 보내는 문제가 있었습니다.
    // AuthorityAuthorizationManager에 RoleHierarchy를 직접 주입해 계층 구조를 적용합니다.
    AuthorityAuthorizationManager<MessageAuthorizationContext<?>> pubManager =
        AuthorityAuthorizationManager.hasRole("USER");
    pubManager.setRoleHierarchy(roleHierarchy);
    AuthorityAuthorizationManager<MessageAuthorizationContext<?>> subManager =
        AuthorityAuthorizationManager.hasRole("USER");
    subManager.setRoleHierarchy(roleHierarchy);

    return new AuthorizationChannelInterceptor(
        MessageMatcherDelegatingAuthorizationManager.builder()
            .nullDestMatcher().permitAll()
            .simpDestMatchers("/pub/**").access(pubManager)
            .simpSubscribeDestMatchers("/sub/**").access(subManager)
            .anyMessage().authenticated()
            .build()
    );
  }

}
