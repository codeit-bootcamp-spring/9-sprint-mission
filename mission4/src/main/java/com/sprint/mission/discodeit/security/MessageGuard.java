package com.sprint.mission.discodeit.security;


import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Component("messageGuard")
public class MessageGuard {

  private final MessageRepository messageRepository;

  @Transactional(readOnly = true)
  public boolean isOwner(UUID messageId, UUID userId) {
    log.info("[MessageGuard] 권한 검증 시작 - 메시지ID:{}, 로그인 유저ID:{}", messageId, userId);

    return messageRepository.findById(messageId)
        .map(message -> {
          if (message.getAuthor() == null) {
            return false;
          }
          UUID authorId = message.getAuthor().getId();
          return authorId.equals(userId);
        })
        .orElse(false);
  }
}
