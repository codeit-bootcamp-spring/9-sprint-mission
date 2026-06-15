package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.secure.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageSecurityService {

  private final MessageService messageService;
  private final MessageRepository messageRepository;

  public boolean isMessageOwner(UUID messageId, DiscodeitUserDetails userDetails) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId()
            .equals(userDetails.getUserDto().id()))
        .orElse(false);
  }

}
