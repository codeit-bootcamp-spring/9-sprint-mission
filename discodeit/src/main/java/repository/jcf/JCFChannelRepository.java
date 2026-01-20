package repository.jcf;

import entity.Channel;
import repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();
    private final Set<String> nameIndex = new HashSet<>();

    @Override
    public Channel save(Channel channel) {
        Channel existing = data.get(channel.getId());

        if (existing == null) {
            data.put(channel.getId(), channel);
            nameIndex.add(channel.getName());
            return channel;
        }

        // name 변경 시 인덱스 갱신
        if (!Objects.equals(existing.getName(), channel.getName())) {
            nameIndex.remove(existing.getName());
            nameIndex.add(channel.getName());
        }

        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        if (channelId == null) return Optional.empty();
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID channelId) {
        if (channelId == null) return;

        Channel removed = data.remove(channelId);
        if (removed == null) return;

        nameIndex.remove(removed.getName());
    }

    @Override
    public boolean existsById(UUID channelId) {
        if (channelId == null) return false;
        return data.containsKey(channelId);
    }
}