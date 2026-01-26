package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFChannelService implements ChannelService{
    private final List<Channel> channels = new ArrayList<>();
    //등록
    @Override
    public Channel create(String name, ChannelType type){
        Channel channel = new Channel(name, type);
        channels.add(channel);
        return channel;
    }
    //단건 조회
    @Override
    public Channel find(UUID channelId){
        for(Channel channel : channels){
            if(channel.getId().equals(channelId)){
                return channel;
            }
        }
        return  null;
    }
    //다건 조회
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    //수정
    @Override
    public Channel update(UUID channelId, String name, ChannelType type) {
        Channel channel = find(channelId);
        if (channel != null) {
            channel.update(name, type);
        }
        return channel;
    }
    //삭제
    @Override
    public void delete(UUID channelId) {
        Channel channel = find(channelId);
        if (channel != null) {
            channels.remove(channel);
        }
    }
}