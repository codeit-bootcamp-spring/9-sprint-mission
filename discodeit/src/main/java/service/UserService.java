package service;

import entity.*;

import java.util.*;

public interface UserService {
    User Create(String name, String phoneNum, String email);

    void Remove(UUID id);

    User findByID(UUID id);

    List<User> getAll();

    void updateName(UUID id, String newName);

    void updatePhoneNumber(UUID id, String newNumber);

    void updateEmail(UUID id, String newEmail);
}
