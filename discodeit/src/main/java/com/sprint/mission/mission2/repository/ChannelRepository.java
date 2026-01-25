package com.sprint.mission.mission2.repository;

import com.sprint.mission.mission2.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel read(UUID id);
    List<Channel> readAll();
    void save(Channel channel);
    void remove(UUID id);
}
