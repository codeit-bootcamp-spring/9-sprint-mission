package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelSevice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelSevice {
    private final List<Channel> data = new ArrayList<>();

    @Override
    public Channel create(String name, String displayname, UUID admin) {
        Channel channel = new Channel(name, displayname, admin);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        for (Channel channel : data) {
            if (channel.getId().equals(channelId)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return data;
    }


    @Override
    public Channel updateDisplayName(UUID channelId, UUID admin, String newdisplayname) {
        Channel channel = find(channelId);
        if (!channel.getAdmin().equals(admin)) {
            throw new IllegalStateException("관리자만 수정 가능");
        }
        channel.changeDisplayName(newdisplayname);
        return channel;
    }

    @Override
    public void delete(UUID channelId, UUID admin) {
        Channel channel = find(channelId);
        if (!channel.getAdmin().equals(admin)) {
            throw new IllegalStateException("관리자만 삭제 가능");
        }
        data.remove(channel);
    }
}

