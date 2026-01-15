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
    public User Create(String name, String phoneNum, String email){
        User newUser = new User(name, phoneNum, email);
        UUID id = newUser.getId();
        userMap.put(id, newUser);
        return newUser;
    }

    @Override
    public void Remove(UUID id){
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

    @Override
    public boolean updateName(UUID id, String newName){
        User user = userMap.get(id);
        if (user == null){
            return false;
        }
        user.UpdateName(newName);
        return true;
    }

    @Override
    public boolean updatePhoneNumber(UUID id, String newNumber){
        User user = userMap.get(id);
        if (user == null){
            return false;
        }
        user.UpdatePhoneNumber(newNumber);
        return true;
    }

    @Override
    public boolean updateEmail(UUID id, String newEmail){
        User user = userMap.get(id);
        if (user == null){
            return false;
        }
        user.UpdateEmail(newEmail);
        return true;
    }
}
