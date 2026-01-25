package repository;

import entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User addUser(String displayName, String email, String phoneNumber);
    User getUser(UUID id);
    List<User> getAllUser();

//    boolean existsById(UUID id);
    void deleteUser(UUID id);

    boolean existsByDisplayName(String displayName);
}