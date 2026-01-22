package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService{
    private final Map<UUID, Channel> data;

    public JCFChannelService(){
        this.data = new HashMap<>();
    }
    @Override
    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID id, Channel newChannel) {
        Channel foundChannel = data.get(id);
        if(foundChannel != null){
            foundChannel.update(newChannel.getChannelName(), newChannel.getDescription());
        }
        return foundChannel;
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }
}
