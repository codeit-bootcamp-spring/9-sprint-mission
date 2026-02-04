package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> nameMap = new ConcurrentHashMap<>();

    private JCFChannelRepository() {}
    private static class Holder {
        private static final JCFChannelRepository INSTANCE = new JCFChannelRepository();
    }
    public static JCFChannelRepository getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        nameMap.put(channel.getName(), channel);
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
    public void delete(UUID id) {
        Channel removed = channelMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getName());
        }
    }
}