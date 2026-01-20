package repository;

import entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);                 // create/update 공용 저장
    Optional<User> findById(UUID userId);
    List<User> findAll();
    void delete(UUID userId);
    boolean existsById(UUID userId);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}