package service.jcf;

import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements UserService {

    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void addUser(User user) {
        data.add(user);
        System.out.println("새로운 멤버가 추가되었습니다: " + user);

    }

    @Override
    public User getUser(String username) {
       return data.stream().filter(user->user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return this.data;
    }

    @Override
    public boolean updateUser(User user) {
        for (User u : data) {
            if (u.getUsername().equals(user.getUsername())) {
                u.update(user.getUsername(), user.getEmail(), user.getPhoneNumber());
                return true;


            }

        }
        return false;


    }

    @Override
    public boolean deleteUser(String Username) {
        return data.removeIf(user -> user.getUsername().equals(Username));

    }
}


