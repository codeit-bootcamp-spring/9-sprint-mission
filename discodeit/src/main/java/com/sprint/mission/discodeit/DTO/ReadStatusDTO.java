package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public class ReadStatusDTO {

    public record CreateReadStatusDTO(UUID messageId, UUID userId, UUID channelId) {
    }

    public record UpdateReadStatusDTO(UUID messageId, UUID userId, UUID channelId) {
    }


}
