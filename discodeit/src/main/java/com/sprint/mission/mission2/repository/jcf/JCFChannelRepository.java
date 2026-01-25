package com.sprint.mission.mission2.repository.jcf;

import com.sprint.mission.mission2.entity.Channel;
import com.sprint.mission.mission2.entity.Message;
import com.sprint.mission.mission2.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel read(UUID id) {
        return channels.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void save(Channel channel) {
        channels.put(channel.getId(), channel);
    }

    @Override
    public void remove(UUID id) {
        channels.remove(id);
    }
}
