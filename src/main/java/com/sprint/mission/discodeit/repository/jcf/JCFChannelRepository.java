package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.UUID;

@Repository
@Profile("jcf")
public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Channel> findAllPublic() {
        return data.values().stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC)
                .toList();
    }

    @Override
    public List<Channel> findPrivateChannelsByUserId(UUID userId) {
        return data.values().stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PRIVATE && channel.isParticipant(userId))
                .toList();
    }

    @Override
    public Optional<Channel> findByName(String name) {
        if (name == null) return Optional.empty();
        return data.values().stream()
                .filter(channel -> channel.getChannelType() == ChannelType.PUBLIC && name.equals(channel.getName()))
                .findFirst();
    }

    @Override
    public Channel update(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
