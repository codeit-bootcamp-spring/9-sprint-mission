package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<UserResponse> findAll();
    boolean existsById(UUID id);
    boolean existsByUsername(String username);//UUID id로 중복을 체크하는게 아니고 이름을 중복체크함
    boolean existsByEmail(String email);
    void deleteById(UUID id);
}
