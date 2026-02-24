package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Channel extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  private String name;
  private ChannelType type;
  private String description;

  private List<UUID> participantIds = new ArrayList<>();

  public Channel(String name, ChannelType type, String description) {
    super();
    this.name = name;
    this.type = type;
    this.description = description;
    this.participantIds = new ArrayList<>();
  }

  public List<UUID> getParticipantIds() {
    if (this.participantIds == null) {
      this.participantIds = new ArrayList<>();
    }
    return this.participantIds;
  }

  public void update(String name, String description) {
    if (this.type == ChannelType.PRIVATE) {
      throw new IllegalStateException("PRIVATE 채널 정보는 수정할 수 없습니다.");
    }
    this.name = name;
    this.description = description;
    recordUpdate();
  }
}