package com.sprint.mission.discodeit.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class S3UploadFailedEvent {
  private final UUID binaryContentId;
  private final String errorMessage;

  public S3UploadFailedEvent(UUID binaryContentId, String errorMessage) {
    this.binaryContentId = binaryContentId;
    this.errorMessage = errorMessage;
  }
}

