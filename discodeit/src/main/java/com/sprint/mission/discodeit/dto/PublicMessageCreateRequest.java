package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record PublicMessageCreateRequest(
    String content,
    UUID channelId,
    UUID authorId
) {

}
