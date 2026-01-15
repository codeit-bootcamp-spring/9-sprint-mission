package service;

import entity.*;

import java.util.*;

public interface UserService {
    User Create(String name, String phoneNum, String email);

    void Remove(UUID id);

    User findByID(UUID id);

    List<User> getAll();

    boolean updateName(UUID id, String newName);

    boolean updatePhoneNumber(UUID id, String newNumber);

    boolean updateEmail(UUID id, String newEmail);
}
