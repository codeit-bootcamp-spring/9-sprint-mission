package Service.jcf;

import Service.ChannelService;
import entity.Channel;
import exception.NotFoundException;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    //생성
    @Override
    public Channel create(String name, UUID ownerId) {
        Channel channel = new Channel(name, ownerId);
        data.put(channel.getId(), channel);
        return channel;
    }

    //수정
    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }
        channel.update(name);
        return channel;
    }

    //단건
    @Override
    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    //전체
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제
    @Override
    public void delete(UUID channelId) {
        if (!data.containsKey(channelId)) {
            throw new NotFoundException("Channel not found. id= " + channelId);
        }
        data.remove(channelId);
    }

    //등록 여부
    @Override
    public boolean exitsById(UUID channelId) {
        return data.containsKey(channelId);
    }
}
