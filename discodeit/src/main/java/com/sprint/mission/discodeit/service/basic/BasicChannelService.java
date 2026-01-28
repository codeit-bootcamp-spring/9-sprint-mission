package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    @Override
    public void create(Channel channel) {
        channelRepository.create(channel);
    }

    @Override
    public Channel findByName(String channelName) {
        return channelRepository.findByName(channelName);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public boolean update(UUID id, String channelName, String channelDescription, boolean isPrivate) {
        return channelRepository.update(id, channelName, channelDescription, isPrivate);
    }

    @Override
    public boolean delete(UUID id) {
        return channelRepository.delete(id);
    }
}

