package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;

  public boolean isResourceOwner(UUID targetUserId, String currentUsername) {
    User user = userRepository.findById(targetUserId).orElse(null);
    if (user == null) {
      return false;
    }
    return user.getUsername().equals(currentUsername);
  }

  public boolean isMessageAuthor(UUID messageId, String currentUsername) {
    Message message = messageRepository.findById(messageId).orElse(null);
    if (message == null) {
      return false;
    }
    return message.getAuthor().getUsername().equals(currentUsername);
  }
}
