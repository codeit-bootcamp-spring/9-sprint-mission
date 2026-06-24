package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SseEventListener {

    private final SseEventProducer sseEventProducer;

    public void sendNotification(List<Notification> notifications) {
        notifications.forEach(notification ->
                sseEventProducer.send(
                        List.of(notification.getReceiverId()),
                        "notifications.created",
                        NotificationDto.form(notification)
                )
        );
    }

    @EventListener
    public void on(ChannelSseEvent event) {
        sseEventProducer.broadcast(event.eventName(), event.channelDto());
    }

    @EventListener
    public void on(UserSseEvent event) {
        sseEventProducer.broadcast(event.eventName(), event.userDto());
    }

    @EventListener
    public void on(BinaryContentSseEvent event) {
        sseEventProducer.send(
                List.of(event.receiverId()),
                "binaryContents.updated",
                event.binaryContentDto()
        );
    }
}