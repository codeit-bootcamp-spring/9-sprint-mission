//package com.sprint.mission.discodeit.listener;
//
//import com.sprint.mission.discodeit.entity.event.MessageCreatedEvent;
//import com.sprint.mission.discodeit.entity.event.RoleUpdatedEvent;
//import com.sprint.mission.discodeit.service.basic.BasicNotificationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class NotificationRequiredEventListener {
//
//  private final BasicNotificationService notificationService;
//
//  @Async("taskExecutor")
//  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//  public void on(MessageCreatedEvent event) {
//    notificationService.createNotificationsForMessage(event);
//  }
//
//  @Async("taskExecutor")
//  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//  public void on(RoleUpdatedEvent event) {
//    notificationService.createNotificationForRoleUpdate(event);
//  }
//}
