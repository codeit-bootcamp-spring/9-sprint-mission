package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private Map<UUID, ReadStatus> store = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) { store.put(readStatus.getId(), readStatus); }

    @Override
    public Optional<ReadStatus> findById(UUID id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : store.values()) {
            if (rs.getUserId().equals(userId)) result.add(rs);
        }
        return result;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus rs : store.values()) {
            if (rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId)) return Optional.of(rs);
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) { store.remove(id); }
}