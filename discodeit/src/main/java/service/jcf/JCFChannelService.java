package service.jcf;

import entity.Channel;
import entity.User;
import service.ChannelService;


import java.util.ArrayList;
import java.util.List;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;
    public JCFChannelService() { this.data = new ArrayList<>();
    }



    @Override
    public boolean addChannel(Channel channel) {
        return false;
    }

    @Override
    public List<Channel> getAllChannel() {
        return List.of();
    }

    @Override
    public Channel updateChannel(String channelName) {
        return null;
    }
}
