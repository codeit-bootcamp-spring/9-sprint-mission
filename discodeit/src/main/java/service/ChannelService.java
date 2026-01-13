package service;

import entity.Channel;
import entity.User;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createChannel(String name,User owner);
    Channel findChannel(String name);
    Channel changeChannel(Channel channel,String name,User owner);
    void addUser(Channel channel,User user);
    List<Channel> AllChannels();
    boolean channelRemove(User owner, String name);

}
