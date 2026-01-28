package service;

import entity.*;

import java.util.*;

public interface UserService {
    User create(String name, String phoneNum, String email);

    void remove(UUID id);

    User findByID(UUID id);

    List<User> getAll();

    User update(UUID userId, String newName, String newNumber, String newEmail);

    User updateName(UUID id, String newName);

    User updatePhoneNumber(UUID id, String newNumber);

    User updateEmail(UUID id, String newEmail);
}
