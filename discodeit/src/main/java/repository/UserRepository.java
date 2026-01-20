package repository;

import entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {

    // 새로 생성되거나 수정된 user를 저장함
    void save(User user);

    boolean remove(UUID id);

    User findByID(UUID id);

    List<User> findAll();
}
