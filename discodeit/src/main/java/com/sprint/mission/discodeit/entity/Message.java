package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseEntity {

  @Column(columnDefinition = "TEXT", nullable = false)
  private String content;

  // 작성자 관계: 다대일
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  // 채널 관계: 다대일
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // 첨부파일 관계: 다대다 (중간 테이블 message_attachments 사용)
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "binary_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, User author, Channel channel, List<BinaryContent> attachments) {
    this.content = content;
    this.author = author;
    this.channel = channel;
    this.attachments = (attachments != null) ? attachments : new ArrayList<>();
  }

  public void update(String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
    }
    this.content = content;
  }
}