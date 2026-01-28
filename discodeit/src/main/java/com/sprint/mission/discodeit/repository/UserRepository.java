// UserRepository.java
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByDisplayName(String displayName);
    Optional<User> findByEmail(String email); // [이게 있어야 빨간줄이 사라집니다]
    List<User> findAll();
    void delete(UUID id);
}