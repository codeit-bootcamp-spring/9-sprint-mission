package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant; // 추가
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DiscordManager implements ChatManager {

  private final UserService userService;
  private final ChannelService channelService;
  private final MessageService messageService;
  private final UserStatusService userStatusService;

  @Override
  public Optional<Message> sendMessage(UUID userId, UUID channelId, String content) {
    if (userService.findById(userId).isEmpty() || channelService.findById(channelId).isEmpty()) {
      return Optional.empty();
    }
    UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest();
    statusRequest.setNewLastActiveAt(Instant.now());
    userStatusService.updateByUserId(userId, statusRequest);
    MessageCreateRequest request = new MessageCreateRequest(
        content,
        userId,
        channelId
    );
    return Optional.ofNullable(messageService.send(request, new ArrayList<>()));
  }

  @Override
  public String getAuthorName(UUID messageId) {
    return messageService.findById(messageId)
        .flatMap(msg -> userService.findById(msg.getAuthorId()))
        .map(user -> user.getUsername())
        .orElse("Unknown");
  }
}