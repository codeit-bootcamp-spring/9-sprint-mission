package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class FileUserStatusRepository implements UserStatusRepository {
    private final Map<UUID, UserStatus> storage = new HashMap<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        storage.put(userStatus.getUserId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return Optional.ofNullable(storage.get(userId));
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return false;
    }
}
