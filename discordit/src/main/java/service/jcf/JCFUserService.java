package service.jcf;

import entity.User;
import service.UserService;

import java.util.*;

public class JCFUserService implements UserService {


    private final Map<UUID, User> data ;


    //생성자에서 초기화
    public JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public boolean addUser(User user) {
        //boolean guess = true;
        boolean flag = data.containsKey(user.getId());
        if(flag){
            System.out.println("이미 존재하는 유저입니다!");
            return false;
        }
        data.put(user.getId(),user);
        return true;

        //boolean flag = data.add(user);
        //if(flag){ return user;}
        //else throw new Exception(); <<이런 방법도 있다 라고 참고만!
    }

    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> getAllUser() {
        return new ArrayList<>(data.values());
    }


    @Override
    public User updateUser(UUID id, String displayName, String email, String phoneNumber) {
        User user = data.get(id);
        if(user!=null){
            user.update(displayName,email,phoneNumber);
        }
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
       User user = data.remove(id);
        System.out.println("유저 정보가 삭제되었습니다.");
    }
}
