package service.jcf;

import entity.User;
import service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class JCFUserService implements UserService {

    private List<User> userList = new ArrayList<>();

    // 1. 생성
    @Override
    public void join(User user) {
        userList.add(user);
    }

    // 2. 단건 조회
    @Override
    public User findById(UUID id) {
        for (User user : userList) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    // 2-1. 다건 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(userList);
    }

    // 3. 수정
    @Override
    public boolean update(UUID id, String nickname, String phoneNumber, String password) {
        for (User u : userList) {
            if (u.getId().equals(id)) {
                u.setNickname(nickname);
                u.setPhoneNumber(phoneNumber);
                u.setPassword(password);
                return true;
            }
        }
        return false;
    }

    // 4. 삭제
    @Override
    public boolean delete(UUID id) {
        for (User u : userList) {
            if (u.getId().equals(id)) {
                return userList.remove(u);
            }
        }
        return false;
    }
}


