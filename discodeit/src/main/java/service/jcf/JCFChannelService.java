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
    public void Remove(UUID id){
        Channel removedChannel = channelMap.remove(id);
        if (removedChannel == null){
            throw new IllegalStateException("채널 삭제 실패 (해당 채널이 존재하지 않음) | 채널ID: " + id);
        }
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
    public boolean updateName(UUID id, String newName) {
        Channel channel = channelMap.get(id);
        if (channel == null){
            return false;
        }
        channel.UpdateName(newName);
        return true;
    }

    @Override
    public boolean addMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        if (channel == null){
            return false;
        }
        return channel.AddMember(user.getId());
    }

    @Override
    public boolean removeMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        if (channel == null){
            return false;
        }
        return channel.RemoveMember(user.getId());
    }

    @Override
    public boolean addMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        if (channel == null){
            return false;
        }
        return channel.AddMessage(message.getId());
    }

    @Override
    public boolean removeMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        if (channel == null){
            return false;
        }
        return channel.RemoveMessage(message.getId());
    }
}
