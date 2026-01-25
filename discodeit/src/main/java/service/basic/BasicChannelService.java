package service.basic;

import entity.Channel;
import entity.User;
import repository.ChannelRepository;
import service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;

public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepository;
    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String name, User owner) {
        Channel channel = channelRepository.findChannel(name);
        if (channel != null) {
                throw new IllegalArgumentException("채널이 이미 존재합니다.");

            }

            return channelRepository.createChannel(name, owner);

    }
    @Override
    public Channel findChannel(String name) {
        if(name==null){
            throw new NoSuchElementException("채널이 없습니다.");
        }
        return channelRepository.findChannel(name);
    }

    @Override
    public Channel changeChannel(Channel channel, String name, User requester) {

        return channelRepository.changeChannel(channel, name, requester);
    }


    @Override
    public void addUser(Channel channel, User user) {
        if(user== null){
            throw new NoSuchElementException("회원이 없습니다.");
        }

        channelRepository.addUser(channel, user);
    }

    @Override
    public List<Channel> AllChannels() {
        return channelRepository.AllChannels();
    }

    @Override
    public boolean channelRemove(Channel channel, User requester) {
        if(!channel.getOwner().getUsername().equals(requester.getUsername())){
            throw new SecurityException("권한이 없습니다.");
        }
        return channelRepository.channelRemove(channel, requester);
    }
}
