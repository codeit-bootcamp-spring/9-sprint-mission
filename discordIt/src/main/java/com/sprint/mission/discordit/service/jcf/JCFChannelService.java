package service.jcf;

import entity.Channel;
import entity.ChannelType;
import service.ChannelService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 인터페이스 구현제, implement를 사용해서 ChannelService에 적어놓은 기능을 수행한다고 약속
public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    // Override : 인터페이스에 적혀있는 기능을 그대로 가져와서 실제 동작을 채워 넣는다는 표시

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel find(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst().orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = find(id);
        if (channel != null) {
            channel.update(name, description);
            return channel;
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = find(id);
        if (channel != null) data.remove(channel);
    }
}