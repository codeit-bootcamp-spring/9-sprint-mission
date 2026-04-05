package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

public record BinaryContentResponse(
    UUID id,
    String fileName,
    Long size,
    String contentType
) {

}

