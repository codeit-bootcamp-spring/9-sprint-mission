package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Channel extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Enumerated(EnumType.STRING)
  private ChannelType type;

  private String name;
  private String description;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;

  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReadStatus> readStatuses = new ArrayList<>();

  public Channel(ChannelType type, String name, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void addReadStatus(ReadStatus readStatus) {
    this.readStatuses.add(readStatus);
  }

  public void update(String newName, String newDescription) {
    if (newName != null) this.name = newName;
    if (newDescription != null) this.description = newDescription;
  }
}