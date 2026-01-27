package main.java.com.sprint.mission.discodeit.service.jcf;

import main.java.com.sprint.mission.discodeit.entity.User;
import main.java.com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    public List<User> data = new ArrayList<>();


    @Override
    public User create(String name, String email, String phoneNumber) {
        User user = new User(name, email, phoneNumber);
        data.add(user);
        return user;
    }

    @Override
    public User update(UUID id, String name, String email, String phoneNumber) {
        User user = findId(id);
        if (user == null) {
            System.out.println("존재하지 않는 사용자입니다.");
            return null;
        }
        user.update(name, email, phoneNumber);
        return user;
    }

    @Override
    public void delete(UUID id) {
        User user = findId(id);
        if(user == null) {
            throw new IllegalArgumentException("없는 정보입니다.");
        }
        data.remove(user);
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public User findId(UUID id) {
        for(User user : data) {
            if(user.getId().equals(id)){
                return user;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 유저입니다.");
    }
}