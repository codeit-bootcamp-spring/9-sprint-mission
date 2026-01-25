package repository.jcf;

import entity.Channel;
import repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final List<Channel> data = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        Objects.requireNonNull(channel, "channel is null");
        UUID id = Objects.requireNonNull(channel.getId(), "channel.id is null");

        for (int i = 0; i < data.size(); i++) {
            if (Objects.equals(data.get(i).getId(), id)) {
                data.set(i, channel);
                return channel;
            }
        }
        data.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (id == null) return Optional.empty();

        for (Channel c : data) {
            if (Objects.equals(c.getId(), id)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data);
    }

    @Override
    public boolean existsById(UUID id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        data.removeIf(c -> Objects.equals(c.getId(), id));
    }
}
