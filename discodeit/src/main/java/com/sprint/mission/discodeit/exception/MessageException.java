package com.sprint.mission.discodeit.exception;

public class MessageException extends DiscodeitException {

  private final String messageId;

  public MessageException(ErrorCode errorCode) {
    super(errorCode);
    this.messageId = null;
  }

  // Map 대신 String messageId를 받도록 수정
  public MessageException(ErrorCode errorCode, String messageId) {
    super(errorCode);
    this.messageId = messageId;
  }

  public String getMessageId() {
    return messageId;
  }
}