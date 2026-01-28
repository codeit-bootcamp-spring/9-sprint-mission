package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

//@Repository
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    // [추가] 이메일로 유저 찾기
    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    // [추가] 디스플레이 이름으로 유저 찾기
    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return userMap.values().stream()
                .filter(user -> user.getDisplayName().equals(displayName))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public void delete(UUID id) {
        userMap.remove(id);
    }
}