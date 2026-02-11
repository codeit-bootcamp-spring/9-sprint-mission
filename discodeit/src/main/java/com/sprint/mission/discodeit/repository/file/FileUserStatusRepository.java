package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {

    private final List<UserStatus> userStatuses = new ArrayList<>();

    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatuses.add(userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatuses.stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        userStatuses.removeIf(status -> status.getUserId().equals(userId));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return userStatuses.stream().anyMatch(status -> status.getUserId().equals(userId));
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return userStatuses.stream().filter(status -> status.getId().equals(id)).findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatuses);
    }
}