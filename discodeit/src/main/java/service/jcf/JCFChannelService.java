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
    public void updateName(UUID id, String newName) {
        Channel channel = channelMap.get(id);
        channel.UpdateName(newName);
    }

    @Override
    public void addMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        channel.AddMember(user.getId());
    }

    @Override
    public void removeMember(UUID channelID, User user) {
        Channel channel = channelMap.get(channelID);
        channel.RemoveMember(user.getId());
    }

    @Override
    public void addMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        channel.AddMessage(message.getId());
    }

    @Override
    public void removeMessage(UUID channelID, Message message) {
        Channel channel = channelMap.get(channelID);
        channel.RemoveMessage(message.getId());
    }
}
