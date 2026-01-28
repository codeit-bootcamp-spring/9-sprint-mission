package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap = new ConcurrentHashMap<>();

    public JCFChannelRepository() {}

    @Override
    public void save(Channel channel) {
        channelMap.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channelMap.values().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void delete(UUID id) {
        channelMap.remove(id);
    }
}