package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.status.ReadStatusInterface;
import com.sprint.mission.discodeit.status.adds.ReadStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFReadStatusRepository implements ReadStatusInterface {

    private final Map<UUID, ReadStatus> store = new HashMap<>();

    @Override
    public void save(ReadStatus readStatus) {
        store.put(readStatus.getId(), readStatus);
    }

    @Override
    public Optional<ReadStatus> findBy(UUID userId, UUID channelId) {
        return store.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)
                && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return store.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void deleteById(UUID id) {
        store.remove(id);
    }

    @Override
    public List<ReadStatus> findByChannel(UUID channelId) {
        return store.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        store.values().removeIf(
                readStatus -> readStatus.getChannelId().equals(channelId)
        );
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return store.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .toList();
    }
}
