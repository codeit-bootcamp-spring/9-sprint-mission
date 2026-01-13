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
    public boolean Remove(UUID id){
        User removedUser = userMap.remove(id);
        if (removedUser == null){
            // 실패
            return false;
        }
        return true;
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
    public void updateName(UUID id, String newName){
        User user = userMap.get(id);
        user.UpdateName(newName);
    }

    @Override
    public void updatePhoneNumber(UUID id, String newNumber){
        User user = userMap.get(id);
        user.UpdatePhoneNumber(newNumber);
    }

    @Override
    public void updateEmail(UUID id, String newEmail){
        User user = userMap.get(id);
        user.UpdateEmail(newEmail);
    }
}
