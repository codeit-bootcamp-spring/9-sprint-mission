package Service.jcf;

import Service.ChannelService;
import entity.Channel;
import exception.NotFoundException;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    //중복검사 인덱스
    private final Set<String> channelNameIndex = new HashSet<>();

    //생성
    @Override
    public Channel create(String name, UUID ownerId) {
        if (channelNameIndex.contains(name)) {
            throw new IllegalArgumentException("Name already exists: " + name);
        }
        Channel channel = new Channel(name, ownerId);
        data.put(channel.getId(), channel);
        channelNameIndex.add(name);

        return channel;
    }

    //수정
    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NotFoundException("Channel not found. id=" + channelId);
        }

        String oldName = channel.getName();

        //채널명 바뀌는 경우에 중복 검사 + 인덱스 갱신
        if (name != null && !oldName.equals(name)) {
            if (channelNameIndex.contains(name)) {
                throw new IllegalArgumentException("Name already exists: " + name);
            }
            channelNameIndex.remove(oldName);
            channelNameIndex.add(name);
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
        Channel channel = data.get(channelId);
        if (!data.containsKey(channelId)) {
            throw new NotFoundException("Channel not found. id= " + channelId);
        }
        data.remove(channelId);
        channelNameIndex.remove(channel.getName());
    }

    //등록 여부
    @Override
    public boolean exitsById(UUID channelId) {
        return data.containsKey(channelId);
    }
}
