package repository.jcf;

import entity.User;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final List<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }

        UUID id = user.getId();
        if (id == null) {
            throw new IllegalStateException("user.id is null (final id should be set in constructor)");
        }

        for (int i = 0; i < data.size(); i++) {
            if (id.equals(data.get(i).getId())) {
                data.set(i, user);   // update
                return user;
            }
        }

        data.add(user);             // insert
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        if (id == null) return Optional.empty();

        for (User user : data) {
            if (id.equals(user.getId())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) return false;

        for (User user : data) {
            if (id.equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;
        data.removeIf(user -> id.equals(user.getId()));
    }
}
