package com.sprint.mission.discodeit.service.basic;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;
@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public Channel save(Channel channel) {
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) { return channelRepository.findById(id); }

    @Override
    public Optional<Channel> findByName(String name) { return channelRepository.findByName(name); }

    @Override
    public List<Channel> findAll() { return channelRepository.findAll(); }

    @Override
    public void update(Channel channel) { channelRepository.save(channel); }

    @Override
    public boolean delete(UUID id) {
        if (channelRepository.findById(id).isPresent()) {
            channelRepository.delete(id);
            return true;
        }
        return false;
    }
}