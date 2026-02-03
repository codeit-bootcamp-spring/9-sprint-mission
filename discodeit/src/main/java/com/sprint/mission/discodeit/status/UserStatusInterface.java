package com.sprint.mission.discodeit.status;

import com.sprint.mission.discodeit.status.adds.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserStatusInterface {
    void save(UserStatus userStatus);
    Optional<UserStatus> findById(UUID id);
    Optional<UserStatus> findByUser(UUID userId);
    List<UserStatus> findAll();
    void deleteById(UUID id);
}
