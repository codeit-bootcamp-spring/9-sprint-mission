package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }

    @Override
    public Channel findByName(String name) {
        for (Channel c : data) {
            if (c.getChannelName().equals(name)) return c;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean update(UUID id, String name, String description, boolean isPrivate) {
        for (Channel c : data) {
            if (c.getId().equals(id)) {
                c.update(name, description, isPrivate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        return data.removeIf(c -> c.getId().equals(id));
    }

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        Channel channel = new Channel(request.name(), request.description(), request.isPrivate());
        create(channel);
        return new ChannelResponse(
                channel.getId(),
                channel.getChannelName(),
                channel.getChannelDescription(),
                channel.isPrivate(),
                null,
                (Instant) null
        );
    }

    @Override
    public ChannelResponse find(UUID channelId) {
        for (Channel c : data) {
            if (c.getId().equals(channelId)) {
                return new ChannelResponse(
                        c.getId(),
                        c.getChannelName(),
                        c.getChannelDescription(),
                        c.isPrivate(),
                        null,
                        (Instant) null
                );
            }
        }
        return null;
    }

    @Override
    public List<ChannelResponse> findAllDto() {
        List<ChannelResponse> result = new ArrayList<>();
        for (Channel c : data) {
            result.add(new ChannelResponse(
                    c.getId(),
                    c.getChannelName(),
                    c.getChannelDescription(),
                    c.isPrivate(),
                    null,
                    (Instant) null
            ));
        }
        return result;
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return findAllDto();
    }
}

