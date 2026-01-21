package repository.jcf;


import entity.User;
import repository.UserRepository;

import java.util.*;


public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();


    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }
}
