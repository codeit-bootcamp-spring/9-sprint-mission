package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class ChannelNotFoundException extends ChannelException {

    public ChannelNotFoundException(Map<String, Object> details) {
        super(ErrorCode.CHANNEL_NOT_FOUND, details);
    }

    public static ChannelNotFoundException withId(UUID channelId) {
        return new ChannelNotFoundException(Map.of("channelId", channelId));
    }
}
