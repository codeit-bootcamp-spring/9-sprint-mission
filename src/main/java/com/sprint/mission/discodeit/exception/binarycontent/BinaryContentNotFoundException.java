package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(Map<String, Object> details) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
    }

    public static BinaryContentNotFoundException withId(UUID binaryContentId) {
        return new BinaryContentNotFoundException(Map.of("binaryContentId", binaryContentId));
    }
}
