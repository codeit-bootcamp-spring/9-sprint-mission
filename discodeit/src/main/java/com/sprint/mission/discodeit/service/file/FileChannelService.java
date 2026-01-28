package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelService delegate;

    public FileChannelService(ChannelService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void create(Channel channel) {
        delegate.create(channel);
    }

    @Override
    public Channel findByName(String name) {
        return delegate.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return delegate.findAll();
    }

    @Override
    public boolean update(UUID id, String name, String description, boolean isPrivate) {
        return delegate.update(id, name, description, isPrivate);
    }

    @Override
    public boolean delete(UUID id) {
        return delegate.delete(id);
    }

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        return delegate.create(request);
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        return delegate.find(channelId);
    }

    @Override
    public List<ChannelResponse> findAllDto() {
        return delegate.findAllDto();
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return delegate.findAllByUserId(userId);
    }
}

