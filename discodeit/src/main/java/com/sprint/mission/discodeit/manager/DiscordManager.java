package com.sprint.mission.discodeit.manager;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService; // [추가]
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
    private final UserStatusService userStatusService; // [추가] 주입

    @Override
    public Optional<MessageDto.Response> sendMessage(UUID userId, UUID channelId, String content) {
        if (userService.findById(userId).isEmpty()) {
            return Optional.empty();
        }

        if (channelService.findById(channelId).isEmpty()) {
            return Optional.empty();
        }

        userStatusService.updateByUserId(userId);

        MessageDto.CreateRequest request = new MessageDto.CreateRequest(
                content, userId, channelId, new ArrayList<>()
        );

        return Optional.of(messageService.send(request));
    }

    @Override
    public String getAuthorName(UUID messageId) {
        return messageService.findById(messageId)
                .flatMap(msg -> userService.findById(msg.authorId()))
                .map(res -> res.displayName())
                .orElse("Unknown");
    }
}