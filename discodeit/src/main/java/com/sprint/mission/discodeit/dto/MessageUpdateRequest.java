package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID id,
        String content
) {}

