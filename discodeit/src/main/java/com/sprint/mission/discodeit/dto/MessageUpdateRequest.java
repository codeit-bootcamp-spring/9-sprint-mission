package com.sprint.mission.discodeit.dto;

import java.util.UUID;
import java.util.List;

public record MessageUpdateRequest(
        UUID id,
        String content,
        List<BinaryContentCreateRequest> attachmentsToAdd
) {}

