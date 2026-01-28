package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> store = new HashMap<>();

    @Override
    public void create(ReadStatus readStatus) {
        store.put(readStatus.getId(), readStatus);
    }

    @Override
    public ReadStatus findById(UUID id) {
        return store.get(id);
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return store.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return store.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return store.values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean delete(UUID id) {
        return store.remove(id) != null;
    }

    @Override
    public int deleteAllByChannelId(UUID channelId) {
        List<UUID> toRemove = store.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .map(ReadStatus::getId)
                .toList();
        toRemove.forEach(store::remove);
        return toRemove.size();
    }
}

