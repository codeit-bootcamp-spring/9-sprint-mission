package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public interface ReadStatusDto {

    record CreateRequest(
            UUID userId,    // 읽은 유저 ID
            UUID channelId  // 읽은 채널 ID
    ) {}

    record Response(
            UUID id,
            UUID userId,
            UUID channelId
    ) {}
}