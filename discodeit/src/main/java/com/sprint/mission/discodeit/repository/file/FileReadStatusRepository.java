package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;

public class FileReadStatusRepository implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> storage = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        storage.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.values().stream()
                .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        List<ReadStatus> list = new ArrayList<>();
        for (ReadStatus rs : storage.values()) {
            if (rs.getUserId().equals(userId)) list.add(rs);
        }
        return list;
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return storage.values().stream()
                .anyMatch(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId));
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
