package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

  public BinaryContentNotFoundException(UUID fileId) {
    super(ErrorCode.BINARY_CONTENT_NOT_FOUND,
        String.format("첨부파일을 찾을 수 없습니다: File ID=%s", fileId));
    addDetail("fileId", fileId);
  }
}