package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class BinaryContentUploadFailedException extends BinaryContentException {

  public BinaryContentUploadFailedException(String fileName, String reason) {
    super(
        ErrorCode.BINARY_CONTENT_UPLOAD_FAILED,
        Map.of("fileName", fileName, "reason", reason)
    );
  }
}

