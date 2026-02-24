package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Message extends BaseEntity {

  private String content;
  private UUID authorId;
  private UUID channelId;

  private List<UUID> attachmentIds = new ArrayList<>();

  public Message(String content, UUID authorId, UUID channelId) {
    super();
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
  }

  public Message(String content, UUID authorId, UUID channelId, List<UUID> attachmentIds) {
    super();
    this.content = content;
    this.authorId = authorId;
    this.channelId = channelId;
    if (attachmentIds != null) {
      this.attachmentIds = attachmentIds;
    }
  }

  public void update(String content) {
    if (content == null || content.isBlank()) {
      throw new IllegalArgumentException("메시지 내용은 비어있을 수 없습니다.");
    }
    this.content = content;
  }
}