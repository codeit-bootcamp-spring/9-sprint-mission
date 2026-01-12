package service;

import entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User Create(String name, String phoneNum, String email);

    User findByID(UUID id);

    List<User> getAll();

    void updateName(UUID id, String newName);

    void updatePhoneNumber(UUID id, String newNumber);

    void updateEmail(UUID id, String newEmail);

    void removeUser(UUID id);
}
