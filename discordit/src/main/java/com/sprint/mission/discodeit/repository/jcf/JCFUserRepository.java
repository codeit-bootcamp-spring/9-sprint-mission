package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {return new ArrayList<>(users);}


    @Override
    public boolean existsById(UUID id) {return findById(id).isPresent();}


    @Override
    public void deleteById(UUID id) {users.removeIf(u -> u.getId().equals(id));}
}