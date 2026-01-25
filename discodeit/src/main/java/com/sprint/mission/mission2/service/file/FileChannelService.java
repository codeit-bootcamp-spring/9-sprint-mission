package com.sprint.mission.mission2.service.file;

import com.sprint.mission.mission2.entity.Channel;
import com.sprint.mission.mission2.repository.ChannelRepository;
import com.sprint.mission.mission2.repository.file.FileChannelRepository;
import com.sprint.mission.mission2.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    ChannelRepository channelRepository = new FileChannelRepository();

    @Override
    public Channel create(String name, UUID ownerId) {
        UUID id = UUID.randomUUID();
        Channel channel = new Channel(id, name, ownerId);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return channelRepository.read(id);
    }

    @Override
    public List<Channel> readAll() {
        return channelRepository.readAll();
    }

    @Override
    public void update(UUID id, String name) {
        Channel channel = channelRepository.read(id);
        if (channel != null) {
            channel.update(name);
        }
        else {
            System.out.println("채널이 존재하지 않습니다");
        }

    }

    @Override
    public void delete(UUID id) {
        channelRepository.remove(id);
    }
}
