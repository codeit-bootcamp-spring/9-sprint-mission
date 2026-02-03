package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class JCFReadStatusRepository implements ReadStatusRepository {

    private final List<ReadStatus> storage = new ArrayList<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        storage.add(readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return storage.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> list = new ArrayList<>();
        for (ReadStatus rs : storage) {
            if (rs.getUserId().equals(userId)) list.add(rs);
        }
        return list;
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.stream()
                .anyMatch(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId));
    }

    @Override
    public void deleteById(UUID id) {
        storage.removeIf(rs -> rs.getId().equals(id));
    }
}
