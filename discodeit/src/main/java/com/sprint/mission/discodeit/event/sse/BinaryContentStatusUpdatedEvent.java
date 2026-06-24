package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BinaryContentStatusUpdatedEvent {

  private final BinaryContentDto binaryContentDto;
}
