package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        channels.removeIf(c -> c.getId().equals(channel.getId()));
        channels.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channels.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {return new ArrayList<>(channels);}


    @Override
    public boolean existsById(UUID id) {return findById(id).isPresent();}


    @Override
    public void deleteById(UUID id) {channels.removeIf(c -> c.getId().equals(id));}

}