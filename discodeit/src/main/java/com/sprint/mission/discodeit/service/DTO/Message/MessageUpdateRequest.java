package com.sprint.mission.discodeit.service.DTO.Message;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID messageId,
        String newContent
) { }
