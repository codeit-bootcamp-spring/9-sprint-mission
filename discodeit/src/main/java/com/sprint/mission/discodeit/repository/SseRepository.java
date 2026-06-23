package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.List;
import java.util.UUID;

public interface SseRepository {

  public UUID save(String eventName, Object data);

  public List<SseMessage> findAfter(UUID lastEventId);
}
