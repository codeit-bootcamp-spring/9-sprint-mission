package repository;

import entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

    public interface ChannelRepository {
        Channel save(Channel channel);
        Optional<Channel> findById(UUID channelId);
        List<Channel> findAll();
        void deleteById(UUID channelId);
        boolean existsById(UUID channelId);

}
