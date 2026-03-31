package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordManager implements ChatManager {

  private final UserService userService;
  private final ChannelService channelService;
  private final MessageService messageService;
  private final UserStatusService userStatusService;

  @Override
  public Optional<MessageDto> sendMessage(UUID userId, UUID channelId, String content) {
    log.info("Manager: Message sending attempt from user {} to channel {}", userId, channelId);
    try {

      userService.findById(userId);
      channelService.findById(channelId);

      UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest(Instant.now());
      userStatusService.updateByUserId(userId, statusRequest);

      MessageCreateRequest request = new MessageCreateRequest(content, userId, channelId);
      MessageDto savedMessage = messageService.send(request, new ArrayList<>());

      log.info("Manager: Message sent successfully. Message ID: {}", savedMessage.id());
      return Optional.ofNullable(savedMessage);

    } catch (DiscodeitException e) {
      log.warn("Manager: Failed to send message - {}", e.getMessage());
      return Optional.empty();
    }
  }

  @Override
  public String getAuthorName(UUID messageId) {
    try {
      MessageDto message = messageService.findById(messageId);

      return message.author().username();
    } catch (DiscodeitException e) {
      log.warn("Manager: Could not find author for message {}", messageId);
      return "Unknown";
    }
  }
}