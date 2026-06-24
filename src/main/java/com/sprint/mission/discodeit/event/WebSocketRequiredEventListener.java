package com.sprint.mission.discodeit.event;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMessage(MessageCreatedEvent event) {
        UUID channelId = event.channelId();
        String messageDto = event.content();
        String destination = "/sub/channels." + channelId + ".messages";

        messagingTemplate.convertAndSend(destination, messageDto);
    }
}
