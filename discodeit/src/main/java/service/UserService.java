package service;

import entity.*;

import java.util.*;

public interface UserService {
    User Create(String name, String phoneNum, String email);

    void Remove(UUID id);

    User findByID(UUID id);

    List<User> getAll();

    User updateName(UUID id, String newName);

    User updatePhoneNumber(UUID id, String newNumber);

    User updateEmail(UUID id, String newEmail);
}
