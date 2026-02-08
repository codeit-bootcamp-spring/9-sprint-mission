package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.ChannelType;


import java.util.List;
import java.util.UUID;

public class ChannelDTO {

    public record CreatePrivateChannelDTO (
            UUID channelId,
            List<UUID> channelMembersIds,
            ChannelType type
    ) { }

    public record CreatePublicChannelDTO (
            String channelName,
            ChannelType type,
            String description,
            UUID channelId
    ) { }

    public record FindPublicChannelDTO (
            UUID channelId,
            long recentMessageCount
    ){ }

    public record FindPrivateChannelDTO (
            UUID channelId,
            UUID channelMembersIds,
            long recentMessageCount
    ){ }

    public record UpdateChannelDTO (
            String channelName,
            ChannelType type,
            String description
    ) { }

}
