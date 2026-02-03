package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.DTO.Channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description);
    Channel createPublic(PublicChannelCreatRequest request);
    Channel createPrivate(PrivateChannelCreatRequest request);
    ChannelFindRespone find(UUID channelId);
    List<Channel> findAll();
    ChannelRespone update(ChannelUpdateRequest request);
    void delete(UUID channelId);
}
