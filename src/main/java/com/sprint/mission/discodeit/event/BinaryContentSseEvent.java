package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.util.UUID;

public record BinaryContentSseEvent(UUID receiverId, BinaryContentDto binaryContentDto) {}