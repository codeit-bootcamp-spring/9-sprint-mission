package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class FileUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        storage.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return storage.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return storage.values().stream()
                .anyMatch(status -> status.getUserId().equals(userId));
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
