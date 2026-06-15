package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageCreatedEventListener {

  private final ReadStatusRepository readStatusRepository;

  /**
   * AFTER_COMMIT: 메시지 저장 트랜잭션이 커밋된 후 실행됩니다.
   * REQUIRES_NEW: 상태 업데이트를 위해 새 트랜잭션을 시작합니다.
   *
   * notificationEnabled=true 인 사용자에게만 알림을 발송합니다.
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleMessageCreated(MessageCreatedEvent event) {
    log.debug("메시지 알림 처리 시작: channelId={}, messageId={}",
        event.channelId(), event.messageId());

    readStatusRepository.findAllByChannelIdWithUser(event.channelId()).stream()
        .filter(readStatus -> readStatus.isNotificationEnabled())
        .forEach(readStatus -> {
          // TODO: 실제 알림 발송 로직 (Push, SSE, WebSocket 등) 으로 교체하세요.
          log.info("알림 발송: userId={}, channelId={}, messageId={}",
              readStatus.getUser().getId(), event.channelId(), event.messageId());
        });

    log.debug("메시지 알림 처리 완료: channelId={}", event.channelId());
  }
}
