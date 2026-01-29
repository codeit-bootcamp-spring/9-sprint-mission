package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public Channel create(ChannelType type, String name, String description, UUID ownerId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("channel name must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("ChannelType must not be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("ownerId must not be null");
        }

        Channel channel = new Channel(type, name, ownerId, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel update(UUID channelId, String description, String name) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found. id=" + channelId));

        if (name != null) {
            if (name.isBlank()) throw new IllegalArgumentException("channel name must not be blank");
        }

        channel.update(description, name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId).orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        channelRepository.deleteById(channelId);
    }

    @Override
    public boolean existsById(UUID channelId) {
        return channelRepository.existsById(channelId);
    }
}