package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationChannelInterceptorTest {

  @Mock
  private JwtRegistry jwtRegistry;

  @Mock
  private UserDetailsService userDetailsService;

  @Test
  void preSend_setsUserOnConnectWhenAccessTokenIsValidAndActive() {
    JwtTokenProvider jwtTokenProvider =
        new JwtTokenProvider("test-secret-key-must-be-at-least-32-bytes", 1800, 3600);
    JwtAuthenticationChannelInterceptor interceptor =
        new JwtAuthenticationChannelInterceptor(jwtTokenProvider, jwtRegistry, userDetailsService);
    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        "ws-user",
        "ws-user@example.com",
        null,
        false,
        Role.USER
    );
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    String token = jwtTokenProvider.generateAccessToken(userDto);
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(true);
    given(userDetailsService.loadUserByUsername("ws-user")).willReturn(userDetails);

    Message<?> result = interceptor.preSend(connectMessage("Bearer " + token), null);

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
    assertThat(accessor).isNotNull();
    assertThat(accessor.getUser()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    assertThat(accessor.getUser().getName()).isEqualTo("ws-user");
  }

  @Test
  void preSend_doesNotSetUserWhenAccessTokenIsNotActive() {
    JwtTokenProvider jwtTokenProvider =
        new JwtTokenProvider("test-secret-key-must-be-at-least-32-bytes", 1800, 3600);
    JwtAuthenticationChannelInterceptor interceptor =
        new JwtAuthenticationChannelInterceptor(jwtTokenProvider, jwtRegistry, userDetailsService);
    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        "inactive-user",
        "inactive-user@example.com",
        null,
        false,
        Role.USER
    );
    String token = jwtTokenProvider.generateAccessToken(userDto);
    given(jwtRegistry.hasActiveJwtInformationByAccessToken(token)).willReturn(false);

    Message<?> result = interceptor.preSend(connectMessage("Bearer " + token), null);

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(result, StompHeaderAccessor.class);
    assertThat(accessor).isNotNull();
    assertThat(accessor.getUser()).isNull();
  }

  private Message<byte[]> connectMessage(String authorization) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
    accessor.setNativeHeader(HttpHeaders.AUTHORIZATION, authorization);
    accessor.setLeaveMutable(true);
    return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
  }
}
