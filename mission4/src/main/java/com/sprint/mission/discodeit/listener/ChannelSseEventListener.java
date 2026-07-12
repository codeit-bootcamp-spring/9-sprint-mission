package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.event.ChannelCreatedEvent;
import com.sprint.mission.discodeit.entity.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.entity.event.ChannelUpdatedEvent;
import com.sprint.mission.discodeit.service.basic.BasicSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 채널 생성/수정/삭제에 대한 SSE 푸시를 담당합니다.
 * BasicChannelService에서 sseService를 직접(트랜잭션 커밋 전에) 호출하던 것을
 * 메시지/알림과 동일하게 AFTER_COMMIT 이후에만 나가도록 분리했습니다.
 * 채널 생성 트랜잭션이 실제로 커밋되기 전에 프런트가 SSE로 새 채널을 통보받고
 * 곧바로 상세 조회/구독을 시도하는 레이스를 없애기 위함입니다.
 */
@Component
@RequiredArgsConstructor
public class ChannelSseEventListener {

  private final BasicSseService sseService;

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelCreatedEvent event) {
    if (event.isBroadcast()) {
      sseService.broadcast("channels.created", event.channel());
    } else {
      sseService.send(event.receiverIds(), "channels.created", event.channel());
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelUpdatedEvent event) {
    sseService.broadcast("channels.updated", event.channel());
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void on(ChannelDeletedEvent event) {
    if (event.isBroadcast()) {
      sseService.broadcast("channels.deleted", event.channelId());
    } else {
      sseService.send(event.receiverIds(), "channels.deleted", event.channelId());
    }
  }
}
