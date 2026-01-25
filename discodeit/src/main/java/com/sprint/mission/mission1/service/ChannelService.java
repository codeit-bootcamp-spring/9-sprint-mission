package com.sprint.mission.mission1.service;

import com.sprint.mission.mission1.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    public Channel create(String name, UUID ownerId);
    public Channel read(UUID id);
    public List<Channel> readAll();
    public void update(UUID id, String name);
    public void delete(UUID id);
}
