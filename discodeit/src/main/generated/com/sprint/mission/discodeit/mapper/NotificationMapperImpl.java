package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-05T17:54:23+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.18 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public NotificationDto toDto(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        UUID receiverId = null;
        UUID id = null;
        Instant createdAt = null;
        String title = null;
        String content = null;

        receiverId = notificationReceiverId( notification );
        id = notification.getId();
        createdAt = notification.getCreatedAt();
        title = notification.getTitle();
        content = notification.getContent();

        NotificationDto notificationDto = new NotificationDto( id, createdAt, receiverId, title, content );

        return notificationDto;
    }

    private UUID notificationReceiverId(Notification notification) {
        User receiver = notification.getReceiver();
        if ( receiver == null ) {
            return null;
        }
        return receiver.getId();
    }
}
