package com.sprint.mission.discodeit.sse;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SseMessage {
  private final UUID id;
  private final String eventName;
  private final Object data;
  private final long createdAt = System.currentTimeMillis();
}
