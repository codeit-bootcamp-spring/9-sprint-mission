package service.jcf;

import entity.Channel;
import entity.User;
import service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels;
    public JCFChannelService() {
        this.channels=new ArrayList<>();
    }
    @Override
    public Channel createChannel(String name, User owner){
        for(Channel channel:channels){
            if(channel.getName().equals(name)){
                return null;
            }
        }
        Channel channel = new Channel(name, owner);
        channels.add(channel);
        return channel;

    }

    @Override
    public Channel findChannel(String name) {
        for(Channel channel:channels){
            if(channel.getName().equals(name)){
                return channel;
            }
        }
        return null;

    }

    @Override
    public Channel changeChannel(Channel channel,String name,User owner) {
            Channel channel1 = findChannel(name);
            if(channel1==null){
                channel.update(name);
                return channel;



            }
           return null;
    }

    @Override
    public void addUser(Channel channel,User user){
         List<User> users=new ArrayList<>();
         users.add(user);
         channel.getMembers().add(user);


    }




    @Override
    public List<Channel> AllChannels(){
        return channels;
    }


    @Override
    public boolean channelRemove(User owner,String name){
        for(Channel channel1:channels){
            if(channel1.getName().equals(name)){
                channels.remove(channel1);
                return true;
            }
        }
        return false;
    }
}
