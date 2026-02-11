package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID lastReadMessageId
) {}

// 새롭게 업데이트할 마지막 읽은 메세지 id