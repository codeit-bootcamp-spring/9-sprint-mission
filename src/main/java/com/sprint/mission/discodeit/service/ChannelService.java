package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.*;

public interface ChannelService {

    Channel create(String name, ChannelType type);
    Channel findById(UUID Id);
    List<Channel> findAll();
    Channel update(UUID id, String name);
    void delete(UUID Id);

}
