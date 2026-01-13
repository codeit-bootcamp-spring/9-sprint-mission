package service.jcf;

import entity.Channel;
import service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService{

    private final Map<UUID, Channel> data;

    public JCFChannelService(){ this.data = new HashMap<>();}


    @Override
    public Channel addChannel(Channel.ChannelType channelType,String channelName, String description) {
        boolean flag  = data.values().stream()
                .anyMatch(channel -> channel.getChannelName().equals(channelName));
        if (flag){
            throw new IllegalStateException("이미 존재하는 채널명입니다.");
        }
        Channel channel = new Channel(channelName, description);
        data.put(channel.getId(),channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) { return data.get(id); }

    @Override
    public List<Channel> getAllChannel() { return new ArrayList<>(data.values()); }

    @Override
    public Channel updateChannel(UUID id, String channelName, String description) {
        Channel channel = data.get(id);
        if (channel!=null){
            channel.updateChannel(channelName,description);
            return channel;
        }
        else {
            return null;
        }
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = data.remove(id);
        System.out.println("======= (DELETE)삭제된 채널 ======= \n" + channel);
    }
}
