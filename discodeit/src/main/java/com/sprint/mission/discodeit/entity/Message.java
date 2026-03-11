package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "message_id")
  private List<BinaryContent> attachments;

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }
}