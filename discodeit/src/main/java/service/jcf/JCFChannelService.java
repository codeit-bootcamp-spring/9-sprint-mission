package service.jcf;

import entity.*;
import service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelMap;

    public JCFChannelService(){
        channelMap = new HashMap<>();
    }

    @Override
    public Channel Create(ChannelType type, String name) {
        Channel newChannel = new Channel(type, name);
        channelMap.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public boolean Remove(UUID id){
        Channel removedChannel = channelMap.remove(id);
        if (removedChannel == null){
            return false;
        }
        return true;
    }

    @Override
    public Channel findByID(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void updateName(UUID id, String newName) {
        Channel channel = channelMap.get(id);
        channel.UpdateName(newName);
    }

    @Override
    public boolean addMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        return channel.AddMember(user.getId());
    }

    @Override
    public boolean removeMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        return channel.RemoveMember(user.getId());
    }

    @Override
    public boolean addMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        return channel.AddMessage(message.getId());
    }

    @Override
    public boolean removeMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        return channel.RemoveMessage(message.getId());
    }
}
