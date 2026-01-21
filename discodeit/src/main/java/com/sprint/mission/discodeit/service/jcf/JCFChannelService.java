package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }
    @Override
    public Channel findByName(String channelName) {
        for (Channel channel : data) {
            if (channel.getChannelName().equals(channelName)) return channel;
            }
        return null;
    }
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        for (Channel c : data) {
            if (c.getId().equals(id)) {
                c.update(channelName, channelDescription, isPrivate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        for (Channel c : data) {
            if (c.getId().equals(id)) {
                return data.remove(c);
            }
        }
        return false;
    }
}
