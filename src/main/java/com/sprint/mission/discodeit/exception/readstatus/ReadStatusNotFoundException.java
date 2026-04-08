package com.sprint.mission.discodeit.exception.readstatus;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ReadStatusNotFoundException extends ReadStatusException {

    public ReadStatusNotFoundException(Map<String, Object> details) {
        super(ErrorCode.READ_STATUS_NOT_FOUND, details);
    }

    public static ReadStatusNotFoundException withId(UUID readStatusId) {
        return new ReadStatusNotFoundException(Map.of("readStatusId", readStatusId));
    }
}
