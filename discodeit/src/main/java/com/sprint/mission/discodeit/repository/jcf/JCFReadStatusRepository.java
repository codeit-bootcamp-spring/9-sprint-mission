package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> readStatusMap;

    public JCFReadStatusRepository(){
        readStatusMap = new HashMap<>();
    }

    @Override
    public void save(ReadStatus readStatus) {
        UUID id = readStatus.getId();
        UUID channelId = readStatus.getChannelId();

        readStatusMap.put(channelId, readStatus);
    }

    @Override
    public boolean remove(UUID id) {
        return readStatusMap.remove(id) != null;
    }

    @Override
    public ReadStatus findByID(UUID id) {
        return readStatusMap.get(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatusMap.values());
    }

    @Override
    public List<ReadStatus> findByChannelID(UUID channelId){
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }
}
