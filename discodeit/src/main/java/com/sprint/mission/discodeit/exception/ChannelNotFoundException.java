package com.sprint.mission.discodeit.exception;

public class ChannelNotFoundException extends ChannelException {
    public ChannelNotFoundException() {
        super(ErrorCode.CHANNEL_NOT_FOUND);
    }
}
