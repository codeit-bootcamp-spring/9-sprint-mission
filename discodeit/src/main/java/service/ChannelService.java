package service;

import entity.Channel;

import java.util.List;

public interface ChannelService {

        boolean addChannel(Channel channel);

        List<Channel> getAllChannel();

        Channel updateChannel(String channelName);


}
