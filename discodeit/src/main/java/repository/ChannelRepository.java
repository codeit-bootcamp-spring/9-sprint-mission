package repository;

import entity.Channel;
import entity.User;

import java.util.List;

public interface ChannelRepository {
    Channel createChannel(String name, User owner);
    Channel findChannel(String name);
    Channel changeChannel(Channel channel,String name,User requester);
    void addUser(Channel channel,User user);
    List<Channel> AllChannels();
    boolean channelRemove(Channel channel,User requester);

}
