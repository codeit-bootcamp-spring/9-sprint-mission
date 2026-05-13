package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.*;

public class JCFUserStatusRepository {
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    public UserStatus save(UserStatus userStatus) {
        this.data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    public Optional<UserStatus> findByUserId(UUID userId) {
        return this.findAll().stream()
                .filter(userStatus -> userStatus.getUser().getId().equals(userId))
                .findFirst();
    }

    public List<UserStatus> findAll() {
        return this.data.values().stream().toList();
    }

    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    public void deleteByUserId(UUID userId) {
        this.findByUserId(userId)
                .ifPresent(userStatus -> this.deleteById(userStatus.getId()));
    }
}
