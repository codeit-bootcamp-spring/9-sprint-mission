package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class MessageAttachmentId {

  @Column(name = "message_id", columnDefinition = "uuid")
  private UUID messageId;

  @Column(name = "attachment_id", columnDefinition = "uuid")
  private UUID attachmentId;

  public MessageAttachmentId(UUID messageId, UUID attachmentId) {
    this.messageId = messageId;
    this.attachmentId = attachmentId;
  }
}
