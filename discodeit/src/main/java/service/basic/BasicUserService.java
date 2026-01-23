package service.basic;

import entity.User;
import repository.UserRepository;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public User update(UUID id, String userName, String email, String phoneNumber) {
       User user = userRepository.findById(id)
               .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
       user.update(userName, email, phoneNumber);
       return userRepository.save(user);
    }

    @Override
    public List<User> checkAll() {
        return List.of();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>();
    }

    @Override
    public User create(String userName, String email, String phoneNumber) {
        User user = new User(userName, email, phoneNumber);
        return userRepository.save(user);
    }

    @Override
    public User findByName(String userName) {
        return null;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found"));
    }

}