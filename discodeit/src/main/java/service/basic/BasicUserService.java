package service.basic;

import entity.User;
import exception.NotFoundException;
import repository.UserRepository;
import service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String displayName, String email, String phoneNumber) {
        // 비즈니스 로직(입력/중복 정책)
        if (displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("phoneNumber must not be blank");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
        }

        User user = new User(displayName, email, phoneNumber);
        return userRepository.save(user);
    }

    @Override
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found. id=" + userId));

        // 비즈니스 로직(변경 시에만 중복 검사)
        if (email != null && !email.equals(user.getEmail())) {
            if (email.isBlank()) throw new IllegalArgumentException("email must not be blank");
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
        }

        if (phoneNumber != null && !phoneNumber.equals(user.getPhoneNumber())) {
            if (phoneNumber.isBlank()) throw new IllegalArgumentException("phoneNumber must not be blank");
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
            }
        }

        if (displayName != null && displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        user.update(displayName, email, phoneNumber);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found. id=" + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }
}