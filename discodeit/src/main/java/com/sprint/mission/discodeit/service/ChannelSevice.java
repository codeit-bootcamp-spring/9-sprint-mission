package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelSevice {
    Channel create(String name, String displayname, UUID admin);

    Channel find(UUID channelId);

    List<Channel> findAll();

    Channel updateDisplayName(UUID channelId, UUID admin, String newdisplayname);

    void delete(UUID channelId, UUID admin);
}
