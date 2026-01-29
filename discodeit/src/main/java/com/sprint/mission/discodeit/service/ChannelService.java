package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(ChannelType type, String name, String description, UUID ownerId);
    Channel update(UUID channelId, String description, String name);
    Channel findById(UUID channelId);
    List<Channel> findAll();
    void delete(UUID channelId);
    boolean existsById(UUID channelId);
}
