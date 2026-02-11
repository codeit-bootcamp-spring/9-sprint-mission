package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
    @Override
    public UserStatus save(UserStatus userStatus) {
        return null;
    }

    @Override
    public List<UserStatus> findAll() {
        return List.of();
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }
}
