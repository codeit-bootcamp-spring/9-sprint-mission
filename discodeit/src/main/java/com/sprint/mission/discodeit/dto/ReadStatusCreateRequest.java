package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID channelId,
        UUID userId,
        UUID lastReadMessageId
) {}

//읽음 상태를 기록할 채널 고유id, 해달 채널을 읽고 있는 유저의 고유id, 유저가 마지막으로 읽은 메세지 위치id