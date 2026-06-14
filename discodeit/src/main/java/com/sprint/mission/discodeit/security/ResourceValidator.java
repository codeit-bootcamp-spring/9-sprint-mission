package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("resourceValidator")
@RequiredArgsConstructor
public class ResourceValidator {

  private final MessageRepository messageRepository;

  public boolean isMessageOwner(DiscodeitUserDetails principal, UUID messageId) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

    return message.getAuthor().getId().equals(principal.getUserDto().id());
  }
}
