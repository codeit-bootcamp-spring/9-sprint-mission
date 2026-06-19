package com.sprint.mission.discodeit.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.JwtRegistry;
import com.sprint.mission.discodeit.security.JwtTokenProvider;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationChannelInterceptorTest {

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private JwtRegistry jwtRegistry;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private MessageChannel messageChannel;

  @InjectMocks
  private JwtAuthenticationChannelInterceptor interceptor;

  @Test
  @DisplayName("CONNECT 프레임 Authorization 헤더의 JWT를 검증하고 사용자 인증 정보를 설정한다")
  void preSend_connect_setsAuthenticatedUser() {
    String token = "access-token";
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserResponse(UUID.randomUUID(), "jun", "jun@test.com", null, true),
        "password"
    );
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
    given(jwtTokenProvider.getUsername(token)).willReturn("jun");
    given(userDetailsService.loadUserByUsername("jun")).willReturn(userDetails);

    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    Message<?> result = interceptor.preSend(message, messageChannel);
    StompHeaderAccessor resultAccessor = StompHeaderAccessor.wrap(result);

    assertThat(resultAccessor.getUser())
        .isInstanceOf(UsernamePasswordAuthenticationToken.class);
  }

  @Test
  @DisplayName("CONNECT 프레임에 Bearer 토큰이 없으면 연결을 거부한다")
  void preSend_connectWithoutBearerToken_throwsException() {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    assertThatThrownBy(() -> interceptor.preSend(message, messageChannel))
        .isInstanceOf(BadCredentialsException.class);
  }

  @Test
  @DisplayName("CONNECT 프레임이 아니면 인증 처리를 수행하지 않는다")
  void preSend_notConnect_returnsMessage() {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SEND);
    accessor.setDestination("/pub/messages");
    Message<?> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

    Message<?> result = interceptor.preSend(message, messageChannel);

    assertThat(result).isSameAs(message);
  }
}
