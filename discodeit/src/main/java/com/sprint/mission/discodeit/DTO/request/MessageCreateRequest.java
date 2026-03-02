package com.sprint.mission.discodeit.DTO.request;


import java.util.UUID;

public record MessageCreateRequest(
        String content,
        UUID channelId,
        UUID authorId
) {

}
