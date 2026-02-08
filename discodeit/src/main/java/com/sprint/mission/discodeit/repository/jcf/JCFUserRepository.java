package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;


public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> store = new HashMap<>();

    @Override
    public User save (User user) {
        store.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findByUserId(UUID userId) {
        return Optional.ofNullable(store.get(userId));
    }

    public Optional<User> findByUserName(String userName) {
        return Optional.ofNullable(store.get(userName));
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(store.values());
    }
    @Override
    public boolean existsById(UUID userId) {
        return store.containsKey(userId);
    }

    @Override
    public void deleteById(UUID userId) {
        store.remove(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return store.containsKey(email);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return store.containsKey(userName);
    }

    @Override
    public boolean existByProfileId(UUID profileId) {
        return store.containsKey(profileId);
    }
}


