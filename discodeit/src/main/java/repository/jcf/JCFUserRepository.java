package repository.jcf;


import entity.User;
import repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class JCFUserRepository implements UserRepository {

    public Map<UUID, User> data;

        UserRepository userRepository = new JCFUserRepository();




    public void deleteById(UUID id) {


    }

    @Override
    public boolean existsById(UUID id) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }
}
