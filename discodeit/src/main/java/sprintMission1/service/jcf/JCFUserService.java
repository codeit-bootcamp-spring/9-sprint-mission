package sprintMission1.service.jcf;

import sprintMission1.entity.User;
import sprintMission1.service.UserService;
import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users;

    private void printUser(User user) {
        System.out.println("--------" + user + "--------");
        System.out.println("UID: " + user.getId());
        System.out.println("이름: " + user.getUserName());
        System.out.println("생성일자: " + user.getCreatedAt());
        System.out.println("수정일자: " + user.getUpdatedAt());
        System.out.println("--------" + user + "--------");
    }

    public JCFUserService() {
        this.users = new HashMap<>();
    }

    public User create(String userName) {
        User user = new User(userName);
        users.put(user.getId(), user);
        return user;
    }

    public void read(UUID userId) {
        User user = users.get(userId);

        if (user != null) {
            printUser(user);
        } else {
            System.out.println("유저가 존재하지 않습니다.");
        }
    }

    public void readAll() {
        for (User user : users.values()) {
            printUser(user);
        }
    }

    public void update(UUID userId, String userName) {
        User user = users.get(userId);
        if (user != null) {
            user.update(userName);
        } else {
            System.out.println("유저가 존재하지 않습니다.");
        }
    }

    public void delete(UUID userId) {
        users.remove(userId);
    }

}