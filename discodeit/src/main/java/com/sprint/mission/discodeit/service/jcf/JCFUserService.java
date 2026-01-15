package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    // 정보 저장소
    private final List<User> data = new ArrayList<>();

    // 정보를 다시 수집하여 재정의 할 수 있도록 Override
    // 유저 객체를 생성

    @Override
    public User create(String name, String email, String phoneNumber) {
        User user = new User(name, email, phoneNumber);
        data.add(user);
        return user;
    }

    @Override
    public User find(UUID id) {
        for (User user : data) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public User update(UUID id, String name, String email, String phoneNumber) {
        User User = find(id);
        if (User == null) {
            System.out.println("유저가 존재하지 않습니다.");
            return null;
        }
        boolean Changed =
                (name != null && !name.equals(User.getName())) ||
                        (email != null && !email.equals(User.getEmail())) ||
                        (phoneNumber != null && !phoneNumber.equals(User.getPhoneNumber()));
        if (!Changed){
            return null;

        }
        User.update(name, email, phoneNumber);
        return User;
    }

    @Override
    public void delete (UUID id){
        User user = find(id);
        if (user == null) {
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }
        data.remove(id);
        }
    }






