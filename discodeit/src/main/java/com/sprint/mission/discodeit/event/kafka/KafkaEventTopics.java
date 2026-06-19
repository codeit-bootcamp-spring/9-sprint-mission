package com.sprint.mission.discodeit.event.kafka;

public final class KafkaEventTopics {

  public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  public static final String S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";

  private KafkaEventTopics() {
  }
}
