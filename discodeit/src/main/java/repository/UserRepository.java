package repository;

import entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface UserRepository {
    Optional<User> findById(UUID id) ;

    User save(User user);
    //Optional<User> find(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}