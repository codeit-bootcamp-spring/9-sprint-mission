package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseUpdatableEntity {

  @Column
  private String content;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", columnDefinition = "uuid", nullable = false)
  private Channel channel;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", columnDefinition = "uuid")
  private User author;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "message_id")
  private List<BinaryContent> attachments;

  public Message(String content, Channel channel, User author) {

    //
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = new ArrayList<>();
  }


  public void update(String newContent) {

    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;

    }


  }
}
