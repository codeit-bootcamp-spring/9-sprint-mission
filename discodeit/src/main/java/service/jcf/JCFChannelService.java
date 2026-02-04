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

        for(Channel channel:channels) {
            if(channel.getName().equals(name)) {
                System.out.println("이미 존재하는 이름입니다.");
                return null;
            }
        }
        Channel channel = new Channel(name, owner);
        channels.add(channel);

        System.out.println(name + "채널이 생성되었습니다. 방장: " + owner.getUsername());
        return channel;

    }

    @Override
    public Channel findChannel(String name) {
        return channels.stream().filter(channel->channel.getName().equals(name))
                .findFirst().orElse(null);


    }

    @Override
    public Channel changeChannel(Channel channel,String name,User requester) {
        if (!channel.getOwner().getUsername().equals(requester.getUsername())) {
            System.out.println("방장만 가능합니다");
            return null;
        }
        Channel channel1 = findChannel(name);
        if(channel1 != null){
            System.out.println("이미 존재하는 이름입니다.");
            return null;
        }else{
            System.out.println("변경 완료 됌");
        }
        channel.update(name);
        return channel;

    }

    @Override
    public void addUser(Channel channel,User user){
         List<User> users=new ArrayList<>();
         users.add(user);
         channel.getMembers().add(user);


    }




    @Override
    public List<Channel> AllChannels(){
        if(channels.size()==0){
            System.out.println("체널이 없습니다.");
        }
        return channels;
    }


    @Override
    public boolean channelRemove(Channel channel,User requester){
      if(!channel.getOwner().getUsername().equals(requester.getUsername())){
          System.out.println("방장만 가능합니다.");
          return false;
      }
      channels.remove(channel);
      return true;
    }
}
