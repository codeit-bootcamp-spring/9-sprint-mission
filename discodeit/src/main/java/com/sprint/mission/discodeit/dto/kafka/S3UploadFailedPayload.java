package com.sprint.mission.discodeit.dto.kafka;

import java.util.UUID;

public record S3UploadFailedPayload(UUID receiverId, String errorMessage) {

}
