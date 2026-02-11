package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }
//매개변수인 message를 가져와 id를 호출하고 그 message의 정보를 데이터에 저장한다. 반환값으로 message를 받는다

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }
//매개변수로 받아온 id에 해당하는 Message가 있으면 데이터에 담아서 반환하고, 없으면 Optional을 반환한다

    @Override
    public List<Message> findAll() {
        return this.data.values().stream().toList();
    }
//data(Map)에 저장된 모든 Message를 List로 만들어서 반환한다

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }
    //id에 해당되는 데이터가 존재하는지 true/false

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
    //id에 해당되는 데이터를 삭제한다

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }
}
//findAllByChannelId 메서드를 stream방식으로 실행하는데 필터를 거쳐서 status의 channelId가 매개변수로 받은 channelId랑 같은면
//리스트로 반환한다