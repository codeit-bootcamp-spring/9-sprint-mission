package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.then;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class MessageWebSocketControllerTest {

  @Mock
  private MessageService messageService;

  @InjectMocks
  private MessageWebSocketController controller;

  @Test
  @DisplayName("STOMP /pub/messages 요청은 첨부파일 없이 메시지를 생성한다")
  void create_delegatesToMessageServiceWithoutAttachments() {
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest request = new MessageCreateRequest(
        "hello",
        UUID.randomUUID(),
        authorId
    );
    UsernamePasswordAuthenticationToken principal = principal(authorId);

    controller.create(request, principal);

    then(messageService).should().create(request, List.of());
  }

  @Test
  @DisplayName("STOMP /pub/messages 요청 작성자와 인증 사용자가 다르면 메시지 생성을 거부한다")
  void create_fail_authorMismatch() {
    MessageCreateRequest request = new MessageCreateRequest(
        "hello",
        UUID.randomUUID(),
        UUID.randomUUID()
    );
    UsernamePasswordAuthenticationToken principal = principal(UUID.randomUUID());

    org.junit.jupiter.api.Assertions.assertThrows(
        AccessDeniedException.class,
        () -> controller.create(request, principal)
    );
  }

  private UsernamePasswordAuthenticationToken principal(UUID userId) {
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(
        new UserResponse(userId, "jun", "jun@test.com", null, true),
        "password"
    );
    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
  }
}
