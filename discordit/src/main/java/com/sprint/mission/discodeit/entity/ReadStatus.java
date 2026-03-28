package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Getter
@EntityScan
public class ReadStatus implements Serializable {

  private final UUID id = UUID.randomUUID();
  private final UUID userId;
  private final UUID channelId;
  private final Instant createAt;
  private Instant updateAt;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId) {
    Instant now = Instant.now();
    this.userId = userId;
    this.channelId = channelId;
    createAt = now;
    updateAt = now;
    lastReadAt = now;
  }

  public void updateReadAt() {
    Instant now = Instant.now();
    updateAt = now;
    lastReadAt = now;
  }
}
