package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelEvent {

  public enum Action {
    CREATED, UPDATED, DELETED
  }

  private final Action action;
  private final ChannelDto channelDto;
}
