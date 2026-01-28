package repository.jcf;

import entity.Channel;
import repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository(){
        channelMap = new HashMap<>();
    }

    @Override
    public void save(Channel channel) {
        UUID id = channel.getId();
        channelMap.put(id, channel);
    }

    @Override
    public boolean remove(UUID id) {
        return channelMap.remove(id) != null;
    }

    @Override
    public Channel findByID(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }
}
