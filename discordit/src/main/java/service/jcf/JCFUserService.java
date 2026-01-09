package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;

public class JCFUserService implements UserService {


    private final Map<UUID, User> data = new HashMap<>();


    //생성자에서 초기화하세요
    /*
    public JCFUserService(){
        this.data = new ArrayList<>();
        this.data = new
    }*/

    @Override
    public boolean addUser(User user) {
        boolean guess = true;
        data.put(user.getId(), user);


        return guess;
        //boolean flag = data.add(user);
        //if(flag){ return user;}
        //else throw new Exception(); <<이런 방법도 있다 라고 참고만!
    }

    @Override
    public User getUser(String displayName) {
        return null;
    }

    @Override
    public List<User> getAllUser() {
        return data;

    }

    @Override
    public User updateUser(String displayName, String email, String phoneNumber) {
        return null;
    }

    @Override
    public boolean deleteUser(String displayName) {
        return false;
    }
}
