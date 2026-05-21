package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageGuard")
@RequiredArgsConstructor
public class MessageGuard {

  private final MessageRepository messageRepository;

  public boolean isAuthor(UUID messageId, DiscodeitUserDetails userDetails) {
    if (userDetails == null) {
      return false;
    }

    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(userDetails.getUserDto().id()))
        .orElse(false);
  }
}
