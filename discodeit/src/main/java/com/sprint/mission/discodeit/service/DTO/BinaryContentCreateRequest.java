package com.sprint.mission.discodeit.service.DTO;

import java.util.UUID;

public record BinaryContentCreateRequest(
        UUID userId,
        UUID messageId
) { }
