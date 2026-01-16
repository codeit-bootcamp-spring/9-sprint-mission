package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> nameMap = new ConcurrentHashMap<>();

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        nameMap.put(channel.getName(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return Optional.ofNullable(nameMap.get(name));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public synchronized void update(Channel newChannel) {
        if (!channelMap.containsKey(newChannel.getId())) return;

        nameMap.entrySet().removeIf(entry -> entry.getValue().getId().equals(newChannel.getId()));
        channelMap.put(newChannel.getId(), newChannel);
        nameMap.put(newChannel.getName(), newChannel);
    }

    @Override
    public boolean delete(UUID id) {
        Channel removed = channelMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getName());
            return true;
        }
        return false;
    }
}