//package com.sprint.mission.discodeit.event;
//
//import com.sprint.mission.discodeit.entity.Notification;
//import com.sprint.mission.discodeit.entity.ReadStatus;
//import com.sprint.mission.discodeit.repository.NotificationRepository;
//import com.sprint.mission.discodeit.repository.ReadStatusRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationRequiredEventListener {
//    private final ReadStatusRepository readStatusRepository;
//    private final NotificationRepository notificationRepository;
//    private final CacheManager cacheManager;
//
//    @Async
//    @TransactionalEventListener
//    public void on(MessageCreatedEvent event) {
//        List<ReadStatus> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrueAndUserIdNot(event.channelId(), event.userId());
//        String title = String.format("%s (#%s)", event.userName(), event.channelName());
//        String content = event.content();
//
//        List<Notification> notifications = readStatuses.stream()
//                .map(readStatus -> new Notification(readStatus.getId(), title, content))
//                .collect(Collectors.toList());
//        notificationRepository.saveAll(notifications);
//
//        Cache cache = cacheManager.getCache("userNotifications");
//        if (cache != null) {
//            for (ReadStatus readStatus : readStatuses) {
//                cache.evict(readStatus.getUser().getId());
//            }
//        }
//    }
//
//    @Async
//    @TransactionalEventListener
//    public void on(RoleUpdatedEvent event) {
//        String title = "권한이 변경되었습니다.";
//        String content = String.format("%s -> %s", event.oldRole(), event.newRole());
//        Notification notification = new Notification(event.userId(), title, content);
//        notificationRepository.save(notification);
//
//        Cache cache = cacheManager.getCache("userNotifications");
//        if (cache != null) {
//            cache.evict(event.userId());
//        }
//    }
//}