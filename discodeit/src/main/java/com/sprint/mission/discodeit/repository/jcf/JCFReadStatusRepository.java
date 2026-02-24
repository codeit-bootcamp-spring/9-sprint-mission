package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> database = new HashMap<>();

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        database.put(readStatus.getId(), readStatus);
        return readStatus;
    }
//매개변수인 readStatus를 가져와 id를 호출하고 그 readStatus 정보를 데이터베이스에 저장한다. 반환값으로 readStatus를 받는다

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }
    //매개변수로 받아온 id에 해당하는 정보가 있으면 데이터에 담아서 반환하고, 없으면 Optional을 반환한다

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return database.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .toList();
    }
    //findAllByUserId 메서드를 stream방식으로 실행하는데 필터를 거쳐서 status의 UserId가 매개변수로 받은 UserId랑 같은면
    //리스트로 반환한다

    @Override
    public boolean existsById(UUID id) {
        return database.containsKey(id);
    }
//existsById메서드를 활용하여 매개변수로 받은 id의 값이 존재하는지 확인한다
    @Override
    public void deleteById(UUID id) {
        database.remove(id);
    }
}
//id(UUID)에 해당하는 데이터를 database에서 삭제한다