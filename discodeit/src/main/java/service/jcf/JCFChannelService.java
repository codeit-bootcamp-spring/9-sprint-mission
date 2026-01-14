package service.jcf;

import entity.Channel;
import service.ChannelService;
import service.MessageService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap = new ConcurrentHashMap<>();
    private final Map<String, Channel> nameMap = new ConcurrentHashMap<>();
    private MessageService messageService;

    public JCFChannelService() {
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        nameMap.put(channel.getName(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return Optional.ofNullable(nameMap.get(name));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void update(Channel channel) {
        Channel oldChannel = channelMap.get(channel.getId());
        if (oldChannel != null) {
            if (!oldChannel.getName().equals(channel.getName())) {
                nameMap.remove(oldChannel.getName());
            }
            channelMap.put(channel.getId(), channel);
            nameMap.put(channel.getName(), channel);
        }
    }

    @Override
    public boolean delete(UUID id) {
        Channel removed = channelMap.remove(id);
        if (removed != null) {
            nameMap.remove(removed.getName());
            if (messageService != null) {
                messageService.deleteByChannelId(id);
            }
            return true;
        }
        return false;
    }
}