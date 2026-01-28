package repository;

import entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    void save(Channel channel);

    boolean remove(UUID id);

    Channel findByID(UUID id);

    List<Channel> findAll();
}
