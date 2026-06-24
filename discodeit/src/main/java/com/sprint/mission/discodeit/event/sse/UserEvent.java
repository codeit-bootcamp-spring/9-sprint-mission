package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserEvent {

  public enum Action {
    CREATED, UPDATED, DELETED
  }

  private final Action action;
  private final UserDto userDto;
}
