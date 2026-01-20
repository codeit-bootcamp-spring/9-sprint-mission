package service.jcf;

import service.UserService;
import entity.User;
import exception.NotFoundException;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    //중복 검사 인덱스
    private final Set<String> emailIndex = new HashSet<>();
    private final Set<String> phoneIndex = new HashSet<>();

    //생성
    @Override
    public User create(String displayName, String email, String phoneNumber) {
        if (emailIndex.contains(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        if (phoneIndex.contains(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
        }

       User user = new User(displayName, email, phoneNumber);

       data.put(user.getId(), user);
       emailIndex.add(email);
       phoneIndex.add(phoneNumber);

       return user;
    }

    //수정
    @Override
    public User update(UUID userId, String displayName, String email, String phoneNumber) {
        User user = data.get(userId);
        if (user == null) {
            throw new NoSuchElementException("User not found. id=" + userId);
        }

        String oldEmail = user.getEmail();
        String oldPhone = user.getPhoneNumber();

        // 이메일 바뀌는 경우에 중복 검사 + 인덱스 갱신
        if (email != null && !oldEmail.equals(email)) {
            if (emailIndex.contains(email)) {
                throw new IllegalArgumentException("Email already exists: " + email);
            }
            emailIndex.remove(oldEmail);
            emailIndex.add(email);
        }

        // 번호 바뀌는 경우에 중복 검사 + 인덱스 갱신
        if (phoneNumber != null && !oldPhone.equals(phoneNumber)) {
            if (phoneIndex.contains(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists: " + phoneNumber);
            }
            phoneIndex.remove(oldPhone);
            phoneIndex.add(phoneNumber);
        }
        user.update(displayName, email, phoneNumber);
        return user;
    }

    //단건
    @Override
    public User findById(UUID userId) {
        User userNullable = this.data.get(userId);

        return Optional.ofNullable(userNullable)
                .orElseThrow(() -> new NoSuchElementException("User with id=" + userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    //삭제
    @Override
    public void delete(UUID userId) {
        User user = data.remove(userId);

        if (user == null) {
            throw new NotFoundException("User not found. id=" + userId);
        }

//        boolean emailRemoved = false;
//        boolean phoneRemoved = false;
//
//        try {
//            emailRemoved = emailIndex.remove(user.getEmail());
//            phoneRemoved = phoneIndex.remove(user.getPhoneNumber());
//        } catch (Exception e) {
//            data.put(userId, user);
//
//            if (emailRemoved) emailIndex.add(user.getEmail());
//            if (phoneRemoved) phoneIndex.add(user.getPhoneNumber());
//
//            throw e; }
        emailIndex.remove(user.getEmail());
        phoneIndex.remove(user.getPhoneNumber());
    }

    //등록여부 확인
    @Override
    public boolean existsById(UUID userId) {
        return data.containsKey(userId);
    }
}
