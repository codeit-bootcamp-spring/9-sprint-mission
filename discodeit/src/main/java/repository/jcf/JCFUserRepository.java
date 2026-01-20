package repository.jcf;

import entity.User;
import repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap;

    public JCFUserRepository(){
        userMap = new HashMap<>();
    }

    @Override
    public void save(User user) {
        UUID id = user.getId();
        userMap.put(id, user);
    }

    @Override
    public boolean remove(UUID id) {
        return (userMap.remove(id) != null);
    }

    @Override
    public User findByID(UUID id) {
        return userMap.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }
}
