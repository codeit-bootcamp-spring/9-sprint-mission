package main.java.com.sprint.mission.discodeit.service.jcf;

import main.java.com.sprint.mission.discodeit.entity.Channel;
import main.java.com.sprint.mission.discodeit.entity.ChannelType;
import main.java.com.sprint.mission.discodeit.service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    public List<Channel> data = new ArrayList<>();


    @Override
    public Channel create(ChannelType type, String channelName, String description) {
        Channel channel = new Channel(channelName, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findId(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id) )
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return data;
    }

    @Override
    public Channel update(UUID id, String channelName, String description) {
        Channel channel = findId(id);
        if(channel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return null;
        }
        channel.update(id, channelName, description);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findId(id);
        if(channel == null) {
            throw new IllegalArgumentException("없는 채널입니다.");
        }
        data.remove(channel);
    }
}
