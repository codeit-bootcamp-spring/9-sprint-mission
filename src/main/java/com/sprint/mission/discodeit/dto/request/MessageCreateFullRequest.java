package com.sprint.mission.discodeit.dto.request;

import java.util.List;

public record MessageCreateFullRequest(
        MessageCreateRequest message,
        List<BinaryContentCreateRequest> attachments
) {
}

