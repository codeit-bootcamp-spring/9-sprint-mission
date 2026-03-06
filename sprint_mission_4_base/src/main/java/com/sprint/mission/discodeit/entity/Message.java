package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "channel_id", nullable = false)
  private UUID channelId;

  @Column(name = "author_id", nullable = false)
  private UUID authorId;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id")
  )
  @Column(name = "attachment_id")
  private List<UUID> attachmentIds;

  public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
    this.content = content;
    this.channelId = channelId;
    this.authorId = authorId;
    this.attachmentIds = attachmentIds;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}