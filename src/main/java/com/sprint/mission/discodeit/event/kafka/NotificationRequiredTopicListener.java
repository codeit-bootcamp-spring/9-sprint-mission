package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;

    @Transactional
    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            log.info("Kafka Consumer - MessageCreatedEvent 수신: {}", event);

            List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrueAndUserIdNot(event.channelId(), event.userId());
            String title = String.format("%s (#%s)", event.userName(), event.channelName());
            String content = event.content();

            List<Notification> notifications = readStatuses.stream()
                    .map(readStatus -> new Notification(
                            readStatus.getUser().getId(),
                            title,
                            content
                    ))
                    .collect(Collectors.toList());
            notificationRepository.saveAll(notifications);
            Cache cache = cacheManager.getCache("userNotifications");
            if (cache != null) {
                for (ReadStatus readStatus : readStatuses) {
                    cache.evict(readStatus.getUser().getId());
                }
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to parse MessageCreatedEvent", e);
            throw new RuntimeException(e);
        }
    }
    @Transactional
    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            log.info("Kafka Consumer - RoleUpdatedEvent 수신: {}", event);

            String title = "권한이 변경되었습니다.";
            String content = String.format("%s -> %s", event.oldRole(), event.newRole());
            Notification notification = new Notification(event.userId(), title, content);
            notificationRepository.save(notification);

            Cache cache = cacheManager.getCache("userNotifications");
            if (cache != null) {
                cache.evict(event.userId());
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse RoleUpdatedEvent", e);
            throw new RuntimeException(e);
        }

    }
    @Transactional
    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
            log.info("Kafka Consumer - S3UploadFailedEvent 수신: {}", event);

            String title = "파일 업로드 실패";
            String content = "파일 업로드 중 오류가 발생했습니다. 다시 시도해주세요.";

            log.warn("S3 업로드 실패 알림 처리 완료: Content ID = {}", event.binaryContentId());

        } catch (JsonProcessingException e) {
            log.error("Failed to parse S3UploadFailedEvent", e);
            throw new RuntimeException(e);
        }
    }
}
