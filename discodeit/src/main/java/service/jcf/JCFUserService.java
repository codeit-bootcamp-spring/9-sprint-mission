package service.jcf;

import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements UserService {

    private final List<User> data;
    public JCFUserService(){
        this.data=new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        data.add(user);
        System.out.println("새로운 멤버가 추가되었습니다: " +user);

    }

    @Override
    public User getUser(String username) {
        for (User user : data) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }

    @Override
    public void updateUser(String Username, String email, String PhoneNumber) {

    }

    @Override
    public boolean deleteUser(String Username) {
        return false;
    }
}
