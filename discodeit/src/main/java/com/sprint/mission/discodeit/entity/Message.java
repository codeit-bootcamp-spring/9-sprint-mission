package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Entity //이 클래스는 데이터베이스 테이블과 연결될 거야
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Message extends BaseUpdatableEntity {

  @Column(nullable = false, length = 1000) //메세지 내용이 꼭 있어야하고 내용은 널널하게
  private String content;

  //UUID대신 진짜 객체로 관계 맺기
  @ManyToOne(fetch = FetchType.LAZY) //여러개의 메세지가 하나의 채널에 속함, lazy는 필요할때만 불러옴
  @JoinColumn(name = "channel_id", nullable = false) //외래키 컬럼만들고 반드시 값 있어야함
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"))
  @Column(name = "attachment_id")
  private List<UUID> attachmentIds;

  public Message(String content, Channel channel, User author, List<UUID> attachmentIds) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachmentIds = attachmentIds;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }

  }
}
