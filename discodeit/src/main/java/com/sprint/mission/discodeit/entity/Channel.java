package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channels")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChannelType type;

  @Column(length = 100, nullable = false)
  private String name;

  @Column(length = 500)
  private String description;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages = new ArrayList<>();

  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void addMessage(Message message) {
    if (message != null) {
      message.assignChannel(this);
    }
  }

  public void removeMessage(Message message) {
    if (message != null && this.messages.contains(message)) {
      message.assignChannel(null);
    }
  }

  public void update(String name, String description) {
    if (name != null && !name.equals(this.name)) {
      this.name = name;
    }
    if (description != null && !description.equals(this.description)) {
      this.description = description;
    }
  }
}
