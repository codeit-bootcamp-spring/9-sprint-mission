package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class DiscordManager implements ChatManager {

  private final UserService userService;
  private final ChannelService channelService;
  private final MessageService messageService;
  private final UserStatusService userStatusService;

  @Override
  public Optional<MessageDto> sendMessage(UUID userId, UUID channelId, String content) {
    try {
      userService.findById(userId);
      channelService.findById(channelId);

      // 활동 시간 업데이트
      UserStatusUpdateRequest statusRequest = new UserStatusUpdateRequest(Instant.now());
      userStatusService.updateByUserId(userId, statusRequest);

      // authorId 대신 메서드 파라미터인 userId를 사용합니다.
      // 순서: content, authorId(userId), channelId
      MessageCreateRequest request = new MessageCreateRequest(content, userId, channelId);

      return Optional.ofNullable(messageService.send(request, new ArrayList<>()));

    } catch (NoSuchElementException e) {
      return Optional.empty();
    }
  }

  @Override
  public String getAuthorName(UUID messageId) {
    try {
      MessageDto message = messageService.findById(messageId);
      return message.author().username(); // username()에서 name()으로 변경
    } catch (NoSuchElementException e) {
      return "Unknown";
    }
  }
}