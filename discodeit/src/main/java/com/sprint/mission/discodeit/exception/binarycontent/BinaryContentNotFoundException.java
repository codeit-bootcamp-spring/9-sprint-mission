package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.DiscodeitException;

import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends DiscodeitException {

  public BinaryContentNotFoundException(UUID binaryContentId) {
    super(
        ErrorCode.BINARY_CONTENT_NOT_FOUND,
        Map.of("binaryContentId", binaryContentId)
    );
  }
}