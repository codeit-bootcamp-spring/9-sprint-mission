package com.sprint.mission.discodeit.dto.data;

import java.util.UUID;

public record SseMessage(
    UUID id,
    String name,
    Object data
) {}
