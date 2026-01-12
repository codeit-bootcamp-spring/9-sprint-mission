package service.jcf;

import entity.*;
import service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private Map<UUID, Channel> channelMap = new HashMap<>();

    public JCFChannelService(){

    }

    @Override
    public Channel Create(ChannelType type, String name) {
        Channel newChannel = new Channel(type, name);
        channelMap.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    @Override
    public void Remove(UUID id){
        channelMap.remove(id);
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
        Channel target = channelMap.get(id);
        target.UpdateName(newName);
        target.updateUpdateAt();
    }

    @Override
    public void addMember(UUID channelID, User user) {
        Channel target = channelMap.get(channelID);
        target.AddMember(user);
        target.updateUpdateAt();
    }

    @Override
    public void removeMember(UUID channelID, User user) {
        Channel target = channelMap.get(channelID);
        target.RemoveMember(user);
        target.updateUpdateAt();
    }

    @Override
    public void addMessage(UUID channelID, Message message) {
        Channel target = channelMap.get(channelID);
        target.AddMessage(message);
        target.updateUpdateAt();
    }

    @Override
    public void removeMessage(UUID channelID, Message message) {
        Channel target = channelMap.get(channelID);
        target.RemoveMessage(message);
        target.updateUpdateAt();
    }
}
