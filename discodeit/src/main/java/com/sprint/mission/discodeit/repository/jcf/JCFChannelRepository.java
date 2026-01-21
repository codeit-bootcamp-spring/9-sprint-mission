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
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel findByName(String channelName) {
        for (Channel channel : data) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        Channel found = findById(id);
        if (found == null) return false;

        found.update(channelName, channelDescription, isPrivate);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        Channel found = findById(id);
        if (found == null) return false;
        return data.remove(found);
    }
}

