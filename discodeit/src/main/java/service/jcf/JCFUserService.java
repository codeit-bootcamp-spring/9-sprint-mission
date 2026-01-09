package service.jcf;

import entity.User;
import service.Usersevice;

import java.util.ArrayList;
import java.util.List;

public class JCFUserService implements Usersevice {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    public boolean addUser(User user) {
        return data.add(user);
    }

//    재정의 후 사용
    @Override
    public User adduuser(User user) {
        return null;
    }

    @Override
    public User getuser(String userName) {
        return null;
    }

    @Override
    public List getAllUser() {
        return List.of();
    }

    @Override
    public User updateUser(String name, String email, String number) {
        return null;
    }

    @Override
    public boolean deleteUser(String userName) {
        return false;
    }
}
