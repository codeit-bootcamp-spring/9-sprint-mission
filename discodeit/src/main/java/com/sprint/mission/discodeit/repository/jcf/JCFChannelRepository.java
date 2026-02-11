package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }

    @Override
    public Channel findById(UUID id) {
        for (Channel c : data) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    @Override
    public Channel findByName(String name) {
        for (Channel c : data) {
            if (c.getChannelName().equals(name)) return c;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        Channel c = findById(id);
        if (c == null) return false;
        c.update(channelName, channelDescription, isPrivate);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        Channel c = findById(id);
        if (c == null) return false;
        return data.remove(c);
    }
}


