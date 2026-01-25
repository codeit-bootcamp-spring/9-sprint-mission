package com.sprint.mission.mission1.service.jcf;

import com.sprint.mission.mission1.entity.Channel;
import com.sprint.mission.mission1.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel create(String name, UUID ownerId) {
        UUID id = UUID.randomUUID();
        Channel channel = new Channel(id, name, ownerId);
        channels.put(id, channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return channels.get(id);
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void update(UUID id, String name) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.update(name);
        } else {
            System.out.println("채널이 존재하지 않습니다");
        }
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channels.remove(id);
        } else {
            System.out.println("채널이 존재하지 않습니다");
        }
    }
}