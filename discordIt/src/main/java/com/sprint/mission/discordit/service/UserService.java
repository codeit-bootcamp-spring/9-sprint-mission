package service;

import entity.User;
import java.util.List;
import java.util.UUID;

// 인터페이스 : 어떤 역할을 수행할 것인지 정해둔 메뉴판, 목록만 정의하는 곳, 실제 행동은 하지 않음
public interface UserService {

    User createUser(String userdname, String email, String password);

    User find(UUID id);

    List<User> findAll();

    User updateUser(UUID id, String username, String email, String password);

    void deleteUser(UUID id);
}