package service.jcf;

import entity.*;
import service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap;

    public JCFUserService(){
        userMap = new HashMap<>();
    }

    @Override
    public User create(String name, String phoneNum, String email){
        User newUser = new User(name, phoneNum, email);
        UUID id = newUser.getId();
        userMap.put(id, newUser);
        return newUser;
    }

    @Override
    public void remove(UUID id){
        User removedUser = userMap.remove(id);
        if (removedUser == null){
            throw new IllegalStateException("유저 삭제 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
    }

    @Override
    public User findByID(UUID id){
        return userMap.get(id);
    }

    @Override
    public List<User> getAll(){
        return new ArrayList<>(userMap.values());
    }

    public User updateAll(UUID id, String newName, String newNumber, String newEmail){
        User user = userMap.get(id);
        if (user == null){
            throw new IllegalStateException("유저 이름 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        user.updateAll(newName, newNumber, newEmail);
        return user;
    }

    @Override
    public User update(UUID userId, String newName, String newEmail, String newPassword){
        // 나중에 구현할거임
        return null;
    }

    @Override
    public User updateName(UUID id, String newName){
        User user = userMap.get(id);
        if (user == null){
            throw new IllegalStateException("유저 이름 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        user.updateName(newName);
        return user;
    }

    @Override
    public User updatePhoneNumber(UUID id, String newNumber){
        User user = userMap.get(id);
        if (user == null){
            throw new IllegalStateException("유저 전화번호 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        user.updatePhoneNumber(newNumber);
        return user;
    }

    @Override
    public User updateEmail(UUID id, String newEmail){
        User user = userMap.get(id);
        if (user == null){
            throw new IllegalStateException("유저 이메일 변경 실패 (해당 유저가 존재하지 않음) | 유저ID: " + id);
        }
        user.updateEmail(newEmail);
        return user;
    }
}
