package service;

import entity.User;

import java.util.List;

public interface UserService {
    void addUser(User user);
    User getUser(String username);
    List<User> getAllUsers();
    void updateUser(String Username,String email,String PhoneNumber);
    boolean deleteUser(String Username);
}
