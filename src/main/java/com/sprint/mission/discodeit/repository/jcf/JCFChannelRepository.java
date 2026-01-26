package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        channels.add(channel);
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public void delete(UUID id) {
        channels.removeIf(channel -> channel.getId().equals(id));
    }
}
