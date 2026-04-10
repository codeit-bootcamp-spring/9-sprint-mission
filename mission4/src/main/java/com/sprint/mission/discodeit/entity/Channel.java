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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Channel extends BaseUpdatableEntity {

  @Enumerated(EnumType.STRING)
  @Column(length = 100, nullable = false)
  private ChannelType type;
  @Column(length = 100)
  private String name;
  @Column(length = 500)
  private String description;

  @BatchSize(size = 100)
  @OneToMany(mappedBy = "channel")
  private List<Message> messages = new ArrayList<>();

  @BatchSize(size = 100)
  @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
  private List<ReadStatus> readStatuses = new ArrayList<>();


  @Builder
  public Channel(ChannelType type, String name, String description) {

    //
    this.type = type;
    this.name = name;
    this.description = description;
  }

  public void update(String newName, String newDescription) {

    if (newName != null && !newName.equals(this.name)) {
      this.name = newName;

    }
    if (newDescription != null && !newDescription.equals(this.description)) {
      this.description = newDescription;

    }


  }
}
