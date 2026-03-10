package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "channels")
@NoArgsConstructor
public class Channel extends BaseEntity {

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChannelType type; // 외부 ChannelType.java 참조

  private Instant lastMessageAt;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Message> messages = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "channel_participants",
      joinColumns = @JoinColumn(name = "channel_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> participants = new ArrayList<>();

  // 내부 Enum 삭제 (중복 정의 제거)

  public Channel(String name, String description, ChannelType type) {
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public void update(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public void updateLastMessageAt(Instant lastMessageAt) {
    this.lastMessageAt = lastMessageAt;
  }

  public void addParticipant(User user) {
    if (user != null && !this.participants.contains(user)) {
      this.participants.add(user);
    }
  }

  public void removeParticipant(User user) {
    this.participants.remove(user);
  }
}