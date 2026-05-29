package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record S3UploadFailedEvent(
    UUID binaryContentId,
    String requestId,
    String taskName,
    String errorMessage
) {

}
