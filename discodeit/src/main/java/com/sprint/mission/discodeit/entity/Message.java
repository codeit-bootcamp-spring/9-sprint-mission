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
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "TEXT")
  private String content;

  @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MessageAttachment> attachments = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false, columnDefinition = "uuid")
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", columnDefinition = "uuid")
  private User author;

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.author = author;
    assignChannel(channel);
  }

  public void assignChannel(Channel channel) {
    if (this.channel != null) {
      this.channel.getMessages().remove(this);
    }
    this.channel = channel;
    if (channel != null && !channel.getMessages().contains(this)) {
      channel.getMessages().add(this);
    }
  }

  public void addAttachment(BinaryContent attachment) {
    MessageAttachment messageAttachment = new MessageAttachment(this, attachment);
    this.attachments.add(messageAttachment);
  }

  public void removeAttachment(MessageAttachment attachment) {
    this.attachments.remove(attachment);
  }

  public void update(String content) {
    if (content != null && !content.equals(this.content)) {
      this.content = content;
    }
  }
}
