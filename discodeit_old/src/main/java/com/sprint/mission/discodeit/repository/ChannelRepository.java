package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    void create(Channel channel);

    Channel findById(UUID id);

    Channel findByName(String channelName);

    List<Channel> findAll();

    boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate);

    boolean delete(UUID id);
}

