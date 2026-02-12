package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageCreateRequest; // 새로운 DTO 임포트
import com.sprint.mission.discodeit.dto.MessageResponse;      // 새로운 DTO 임포트
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public Optional<MessageResponse> sendMessage(UUID userId, UUID channelId, String content) {
        if (userService.findById(userId).isEmpty() || channelService.findById(channelId).isEmpty()) {
            return Optional.empty();
        }
        userStatusService.updateByUserId(userId);
        MessageCreateRequest request = new MessageCreateRequest(
                content,
                userId,
                channelId,
                new ArrayList<>()
        );
        return Optional.of(messageService.send(request));
    }

    @Override
    public String getAuthorName(UUID messageId) {
        return messageService.findById(messageId)
                .flatMap(msg -> userService.findById(msg.authorId()))
                .map(userDto -> userDto.username()) // displayName() -> username()으로 변경
                .orElse("Unknown");
    }
}