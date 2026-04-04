package com.sprint.mission.discodeit.exception.binaryContent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class BinaryContentTypeInvalid extends BinaryContentException{

  public BinaryContentTypeInvalid(ErrorCode errorCode,
      Map<String, Object> details) {
    super(errorCode, details);
  }

  public BinaryContentTypeInvalid() {
    super(ErrorCode.BINARY_CONTENT_TYPE_INVALID);
  }
}
