package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentAlreadyExistsException extends BinaryContentException {

  public BinaryContentAlreadyExistsException(UUID binaryContentId) {
    super(
        ErrorCode.BINARY_CONTENT_ALREADY_EXISTS,
        Map.of("binaryContentId", binaryContentId)
    );
  }
}
