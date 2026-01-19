package service.jcf;

import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public boolean addUser(User user) {
        return data.add(user);

    }

    @Override
    public User getUser(String displayName) {
        for (User user : data) {
            if (user.getdisplayName().equals(displayName)) {
                return user; // 찾으면 바로 반환
            }
        }
        return null; // 못 찾으면 null
    }

    @Override
    public User getUserById(String userId) {
        for (User user : data) {
            if (user.getById().equals(userId)) {
                return user;
            }
        }
        return null;
    }


    @Override
    public List<User> getallUser() {
        return data;
    }



    @Override
    public User updateUser(String olddisplayName, String newdisplayName, String email, String phoneNumber) {
        for (User user : data) {
            if (user.getdisplayName().equals(olddisplayName)) {
                user.setdisplayName(newdisplayName);
                user.setEmail(email);
                user.setPhoneNumber(phoneNumber);
                user.setUpdatedAt(System.currentTimeMillis());
                return user;
            }
        }
        return null;
    }


    @Override
    public boolean deleteUser(String username) {
        return data.removeIf(user -> user.getdisplayName().equals(username));
    }

    @Override
    public User getbyId(UUID userId) {
        for (User user : data) {
            if (user.getId().equals(userId)) {   // ✅ 보통 User는 getId()
                return user;
            }
        }
        return null;
    }
}



