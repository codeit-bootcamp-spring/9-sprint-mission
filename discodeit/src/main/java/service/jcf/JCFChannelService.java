package service.jcf;

import service.ChannelService;
import entity.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private List<Channel> channelList = new ArrayList<>();

    @Override
    public void create(Channel channel) {
        channelList.add(channel);
    }
    @Override
    public Channel findByName(String channelName) {
        for (Channel channel : channelList) {
            if (channel.getChannelName().equals(channelName)) {
                return channel;
            }
        }
        return null;
    }
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelList);
    }

    @Override
    public boolean update(UUID id, String ChannelName, String ChannelDescription, boolean isPrivate) {
        for (Channel c : channelList) {
            if (c.getId().equals(id)) {
                c.setChannelName(ChannelName);
                c.setChannelDescription(ChannelDescription);
                c.setPrivateChannel(isPrivate);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(UUID id) {
        for (Channel c : channelList) {
            if (c.getId().equals(id)) {
                return channelList.remove(c);
            }
        }
        return false;
    }
}
