package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_attachments")
@IdClass(MessageAttachment.MessageAttachmentId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAttachment {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id", nullable = false, columnDefinition = "uuid")
  private Message message;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attachment_id", nullable = false, columnDefinition = "uuid")
  private BinaryContent attachment;

  public MessageAttachment(Message message, BinaryContent attachment) {
    this.message = Objects.requireNonNull(message);
    this.attachment = Objects.requireNonNull(attachment);
  }

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @EqualsAndHashCode
  public static class MessageAttachmentId implements Serializable {

    private UUID message;
    private UUID attachment;

    public MessageAttachmentId(UUID message, UUID attachment) {
      this.message = message;
      this.attachment = attachment;
    }
  }
}
