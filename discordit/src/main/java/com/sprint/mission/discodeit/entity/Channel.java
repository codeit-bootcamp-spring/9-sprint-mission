package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Getter
@EntityScan
public class Channel implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;
  private final UUID id;
  private final Instant createAt;
  private final Map<String, UUID> participantIds = new ConcurrentHashMap<>();
  private final Map<String, ReadStatus> userReadStatusMap = new ConcurrentHashMap<>();
  private final ChannelType channelType;
  private String name;
  private String description;
  private Instant updateAt;

  public Channel(String name, String description, ChannelType channelType,
      List<UUID> participantIds) {
    Instant now = Instant.now();
    this.id = UUID.randomUUID();
    this.name = name;
    this.channelType = channelType;
    this.description = description;
    this.createAt = now;
    this.updateAt = now;
  }

  /// setter
  private void setUpdateAt() {
    this.updateAt = Instant.now();
  }

  public void channelUpdater(String name, String description) {
    boolean edit = false;

    if (name != null && !name.isEmpty() && !this.name.equals(name)) {
      this.name = name;
      edit = true;
    }

    if (description != null && !description.isEmpty() && !this.description.equals(description)) {
      this.description = description;
      edit = true;
    }

    if (edit) {
      setUpdateAt();
    }
  }

  public void addParticipantIds(String username, UUID userId) {
    participantIds.put(username, userId);
  }

  public void removeParticipantIds(String username) {
    participantIds.remove(username);
  }
}