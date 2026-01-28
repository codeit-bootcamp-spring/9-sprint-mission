package service.Basic;

import entity.User;
import repository.UserRepository;
import service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String phoneNum, String email) {
        User newUser = new User(name, phoneNum, email);
        userRepository.save(newUser);
        return newUser;
    }

    @Override
    public void remove(UUID id) {
        userRepository.remove(id);
    }

    @Override
    public User findByID(UUID id) {
        return userRepository.findByID(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String newName, String newEmail, String newPassword) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 이름 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.update(newName, newEmail, newPassword);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updateName(UUID id, String newName) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 이름 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updateName(newName);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updatePhoneNumber(UUID id, String newNumber) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 전화번호 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updatePhoneNumber(newNumber);
        userRepository.save(target);
        return target;
    }

    @Override
    public User updateEmail(UUID id, String newEmail) {
        User target = userRepository.findByID(id);
        if (target == null) {
            throw new IllegalStateException("유저 이메일 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        target.updateEmail(newEmail);
        userRepository.save(target);
        return target;
    }
}
