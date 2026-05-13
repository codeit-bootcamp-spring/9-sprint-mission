package com.sprint.mission.discodeit.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

class LoginSuccessHandlerTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final LoginSuccessHandler handler = new LoginSuccessHandler(objectMapper);

  @Test
  @DisplayName("인증 성공 시 200 UserDto로 응답한다")
  void onAuthenticationSuccess_WritesUserDto() throws Exception {
    UserDto userDto = new UserDto(UUID.randomUUID(), "testuser", "test@example.com", null, true);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "$2a$10$password");
    UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
        .authenticated(userDetails, null, userDetails.getAuthorities());
    MockHttpServletResponse response = new MockHttpServletResponse();

    handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
    assertThat(objectMapper.readValue(response.getContentAsString(), UserDto.class))
        .isEqualTo(userDto);
  }
}
