package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFReadStatusRepository implements ReadStatusRepository {

    private final List<ReadStatus> readStatuses = new ArrayList<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        readStatuses.add(readStatus);
        return readStatus;
    }

    @Override
    public ReadStatus find(UUID id) {
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getId().equals(id)) {
                return readStatus;
            }
        }
        return null;
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getUserId().equals(userId) && readStatus.getChannelId().equals(channelId)) {
                return readStatus;
            }
        }
        return null;
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        for (ReadStatus readStatus : readStatuses) {
            if (readStatus.getUserId().equals(userId)) {
                result.add(readStatus);
            }
        }
        return result;
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(readStatuses);
    }

    @Override
    public void delete(UUID id) {
        readStatuses.removeIf(readStatus -> readStatus.getId().equals(id));
    }
}
