package service.jcf;

import entity.User;
import service.JCFMessageService;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements UserService {

    private final List<User> data;
    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public User addUser(User user) {
        boolean flag = data.add(user);
        if(flag){
            return user;
        }else{
            throw new Exception;
        }
    }

    @Override
    public User getUser(String displayName) {
        return data;
    }

    @Override
    public List<User> getAllUser() {
        return List.of();
    }

    @Override
    public User updateUser(String name, String email, String phoneNumber) {
        return null;
    }

    @Override
    public boolean deleteUser(User displayName) {
        return false;
    }
}
