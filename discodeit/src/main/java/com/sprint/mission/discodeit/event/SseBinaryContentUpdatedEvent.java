package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;

public record SseBinaryContentUpdatedEvent(
    BinaryContentResponse binaryContent
) {

}
