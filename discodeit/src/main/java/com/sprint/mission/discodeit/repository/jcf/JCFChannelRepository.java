package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        this.data.put(channel.getId(), channel);
        return channel;
    }
//매개변수인 Channel를 가져와 id를 호출하고 그 Channel의 정보를 데이터에 저장한다. 반환값으로 Channel를 받는다

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }
//매개변수로 받아온 id에 해당하는 Channel 있으면 데이터에 담아서 반환하고, 없으면 Optional을 반환한다

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }
//data(Map)에 저장된 모든 Channel를 List로 만들어서 반환한다

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }
    //id에 해당되는 데이터가 존재하는지 true/false

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
//id에 해당되는 데이터를 삭제한다