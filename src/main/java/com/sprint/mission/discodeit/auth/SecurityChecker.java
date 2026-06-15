package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityChecker {

    private final MessageRepository messageRepository;

    public boolean isMessageAuthor(UUID messageId, UUID userId) {
        return messageRepository.findById(messageId)
                .map(message -> message.getAuthor().getId().equals(userId))
                .orElse(false);
    }
}
