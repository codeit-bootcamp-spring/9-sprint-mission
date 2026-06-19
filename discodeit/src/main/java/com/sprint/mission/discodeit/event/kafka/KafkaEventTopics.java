package com.sprint.mission.discodeit.event.kafka;

public final class KafkaEventTopics {

  public static final String MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  public static final String ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  public static final String S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";
  public static final String REALTIME_MESSAGE_CREATED = "discodeit.realtime.MessageCreatedEvent";
  public static final String REALTIME_SSE_NOTIFICATION_CREATED =
      "discodeit.realtime.SseNotificationCreatedEvent";
  public static final String REALTIME_SSE_BINARY_CONTENT_UPDATED =
      "discodeit.realtime.SseBinaryContentUpdatedEvent";
  public static final String REALTIME_SSE_CHANNEL_CHANGED =
      "discodeit.realtime.SseChannelChangedEvent";
  public static final String REALTIME_SSE_USER_CHANGED =
      "discodeit.realtime.SseUserChangedEvent";

  private KafkaEventTopics() {
  }
}
