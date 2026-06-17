package com.sprint.mission.discodeit.entity.event;

import java.util.UUID;

public record S3UploadFailedEvent(
    String fileKey,
    UUID uploaderId
) {

}
