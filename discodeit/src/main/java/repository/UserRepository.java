package repository;

import entity.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);
    User getUser(String username);
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(String Username);
}

