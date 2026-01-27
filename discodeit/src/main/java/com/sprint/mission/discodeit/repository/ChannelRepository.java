package main.java.com.sprint.mission.discodeit.repository;

import main.java.com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> findId(UUID channelId);
    List<Channel> findAll();
    void deleteById(UUID id);

}
