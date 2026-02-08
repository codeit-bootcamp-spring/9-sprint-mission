package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

//    Channel createChannel(ChannelType type, String channelName, String description);
    Channel createPublicChannel(ChannelDTO.CreatePublicChannelDTO dto);
    Channel createPrivateChannel(ChannelDTO.CreatePrivateChannelDTO dto);

    Channel findByChannelId(UUID ChannelId);

    List<Channel> findAllChannel();

    Channel updateChannel(UUID ChannelId, String channelName, ChannelType type, String description);

    boolean deleteChannel(UUID ChannelId);

}
