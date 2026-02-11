package com.sprint.mission.discodeit.dto;

import java.util.List;
import java.util.UUID;

public record CreateMessageDto(
        String text,
        String sendeeChannelName,
        String senderUserName,
        List<UUID> binaryContentIds
) {
}
