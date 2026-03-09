package com.sprint.mission.discodeit.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "message_attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageAttachment {

  @EmbeddedId
  private MessageAttachmentId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("messageId")
  @JoinColumn(name = "message_id", nullable = false, columnDefinition = "uuid")
  private Message message;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("attachmentId")
  @JoinColumn(name = "attachment_id", nullable = false, columnDefinition = "uuid")
  private BinaryContent attachment;

  public MessageAttachment(Message message, BinaryContent attachment) {
    this.message = Objects.requireNonNull(message);
    this.attachment = Objects.requireNonNull(attachment);
    this.id = new MessageAttachmentId(message.getId(), attachment.getId());
  }
}