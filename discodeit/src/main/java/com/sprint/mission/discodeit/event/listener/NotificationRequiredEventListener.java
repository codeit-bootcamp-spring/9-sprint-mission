package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.message.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
//@Component
public class NotificationRequiredEventListener {

  private final NotificationService notificationService;
  private final ReadStatusRepository readStatusRepository;
  private final ChannelService channelService;
  private final UserRepository userRepository;

  @Value("${discodeit.admin.username}")
  private String adminUsername;


  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    MessageDto message = event.getData();
    UUID channelId = message.channelId();
    ChannelDto channel = channelService.find(channelId);

    Set<UUID> receiverIds = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
            channelId)
        .stream().map(readStatus -> readStatus.getUser().getId())
        .filter(receiverId -> !receiverId.equals(message.author().id()))
        .collect(Collectors.toSet());
    String title = message.author().username()
        .concat(
            channel.type().equals(ChannelType.PUBLIC) ?
                String.format(" (#%s)", channel.name()) : ""
        );
    String content = message.content();

    notificationService.create(receiverIds, title, content);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    UUID userId = event.getUserId();
    Role from = event.getFrom();
    Role to = event.getTo();

    String title = "권한이 변경되었습니다.";
    String content = String.format("%s -> %s", from.name(), to.name());

    notificationService.create(Set.of(userId), title, content);
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    String requestId = event.getRequestId();
    UUID binaryContentId = event.getBinaryContentId();
    Throwable e = event.getE();

    String title = "S3 파일 업로드 실패";

    StringBuffer sb = new StringBuffer();
    sb.append("RequestId: ").append(requestId).append("\n");
    sb.append("BinaryContentId: ").append(binaryContentId).append("\n");
    sb.append("Error: ").append(e.getMessage()).append("\n");
    String content = sb.toString();

    Set<UUID> receiverIds = userRepository.findByUsername(adminUsername)
        .map(user -> Set.of(user.getId()))
        .orElse(Set.of());

    notificationService.create(receiverIds, title, content);
  }
}
