package main.java.com.sprint.mission.discodeit.service;

import main.java.com.sprint.mission.discodeit.entity.Channel;
import main.java.com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(ChannelType type, String channelName, String description);
    Channel findId(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String channelName, String description);
    void delete(UUID uuid);

}
