package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        if (readStatus == null) {
            throw new IllegalArgumentException("readStatus is null");
        }
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (rs != null && userId.equals(rs.getUserId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            return List.of();
        }

        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus rs : data.values()) {
            if (rs != null && channelId.equals(rs.getChannelId())) {
                result.add(rs);
            }
        }
        return result;
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        if (userId == null || channelId == null) {
            return Optional.empty();
        }

        for (ReadStatus rs : data.values()) {
            if (rs != null && userId.equals(rs.getUserId()) && channelId.equals(rs.getChannelId())) {
                return Optional.of(rs);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(UUID id) {
        if (id == null) return;
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;
        return data.containsKey(id);
    }
}
