package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class PrivateChannelUpdateException extends ChannelException {

    public PrivateChannelUpdateException(Map<String, Object> details) {
        super(ErrorCode.PRIVATE_CHANNEL_UPDATE, details);
    }

    public static PrivateChannelUpdateException withId(UUID channelId) {
        return new PrivateChannelUpdateException(Map.of("channelId", channelId));
    }
}
