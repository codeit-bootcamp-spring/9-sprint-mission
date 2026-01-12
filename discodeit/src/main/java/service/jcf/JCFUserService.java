package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;
import java.util.stream.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userMap = new HashMap<>();

    public JCFUserService(){

    }

    @Override
    public User Create(String name, String phoneNum, String email){
        User newUser = new User(name, phoneNum, email);
        UUID id = newUser.getId();
        userMap.put(id, newUser);
        return newUser;
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
        User target = userMap.get(id);
        target.UpdateName(newName);
        target.updateUpdateAt();
    }

    @Override
    public void updatePhoneNumber(UUID id, String newNumber){
        User target = userMap.get(id);
        target.UpdateName(newNumber);
        target.updateUpdateAt();
    }

    @Override
    public void updateEmail(UUID id, String newEmail){
        User target = userMap.get(id);
        target.UpdateName(newEmail);
        target.updateUpdateAt();
    }

    @Override
    public void removeUser(UUID id){
        userMap.remove(id);
    }

}
