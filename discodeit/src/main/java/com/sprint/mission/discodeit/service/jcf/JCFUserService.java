package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService{

    private final Map<UUID, User> data;

    public JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(UUID id, User newUser) {
        User foundUser = data.get(id);

        if (foundUser != null) {
            // foundUser(진짜)의 정보를 newUser(요청정보)의 내용으로 갈아끼웁니다.
            if (newUser.getNickname() != null) {
                foundUser.updateNickname(newUser.getNickname());
            }

            // 2. 새 비밀번호가 있으면 -> 비밀번호 변경 메서드 호출
            if (newUser.getPassword() != null) {
                foundUser.updatePassword(newUser.getPassword());
            }
        }

        return foundUser;
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }
}
