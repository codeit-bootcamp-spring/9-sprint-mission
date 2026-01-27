package main.java.com.sprint.mission.discodeit.repository;

import main.java.com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findId(UUID userId);
    List<User> findAll();
    void deleteById(UUID id);

}
