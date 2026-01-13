package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Channel name cannot be null or empty");
        }

        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("Channel with id " + id + " does not exist");
        }

        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("Channel with id " + id + " does not exist");
        }

        if (name == null) {
            throw new IllegalArgumentException("Channel name cannot be null");
        }

        channel.updateName(name);

        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = data.get(id);

        if (channel == null) {
            throw new IllegalArgumentException("Channel with id " + id + " does not exist");
        }

        data.remove(id);
    }
}
